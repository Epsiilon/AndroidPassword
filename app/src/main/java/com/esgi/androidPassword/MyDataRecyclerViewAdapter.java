package com.esgi.androidPassword;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.esgi.androidPassword.DataFragment.OnListFragmentInteractionListener;
import com.esgi.androidPassword.dummy.DummyContent.DummyItem;
import com.esgi.androidPassword.util.PasswordUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static com.esgi.androidPassword.constant.AndroidPasswordConstant.*;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyDataRecyclerViewAdapter extends RecyclerView.Adapter<MyDataRecyclerViewAdapter.ViewHolder>
        implements EventListener<QuerySnapshot> {


    private final List<DummyItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Query query;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;

    public MyDataRecyclerViewAdapter(Query query, OnListFragmentInteractionListener listener) {
        mValues = new ArrayList<>();
        mListener = listener;
        this.query = query;
    }

    public void startListening() {
        if (query != null && registration == null) {
            registration = query.addSnapshotListener(this);
        }
    }

    public void stopListenning() {
        if (registration != null) {
            registration.remove();
            registration = null;
        }
        mValues.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_data, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).title);
        holder.mUsername.setText(mValues.get(position).username);
        holder.mPassword.setText(STAR);
        holder.mNotes.setText(mValues.get(position).notes);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                DocumentReference user = db.collection(DATAS).document(mValues.get(position).id);
                user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(MY_DATA_RECYCLER, DOCUMENT_SNAPSHOT_SUCCESSFULLY_DELETED);
                        Toast.makeText(
                                v.getContext(),
                                v.getContext().getString(R.string.success_delete),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(ERROR_DURING_REQUEST,  e.getMessage());
                        Toast.makeText(
                                v.getContext(),
                                v.getContext().getString(R.string.error_suppress),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

            }
        });


        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent (v.getContext(), EditActivity.class);
                intent.putExtra("idData", mValues.get(position).getId());
                v.getContext().startActivity(intent);
            }
        });

        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) view.getContext()
                        .getSystemService(Context.CLIPBOARD_SERVICE);

                try {
                    String password = PasswordUtils.decryptFromAES(mValues.get(position).password);
                    cm.setText(password);

                    StringBuilder txtToast = new StringBuilder();
                    txtToast.append(COPIE);
                    txtToast.append(password);

                    Toast.makeText(view.getContext(), txtToast, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e(RECYCLER_VIEW, ERROR_DURING_DESCRIPTING_PASSWORD, e);
                }
            }
        });

        holder.showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (STAR.equals(holder.mPassword.getText())) {
                    try {
                        holder.mPassword.setText(PasswordUtils.decryptFromAES(mValues.get(position).password));
                    } catch (Exception e) {
                        Log.e(RECYCLER_VIEW, ERROR_DURING_DESCRIPTING_PASSWORD, e);
                    }
                } else {
                    holder.mPassword.setText(STAR);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public void onEvent(
            @Nullable QuerySnapshot queryDocumentSnapshots,
            @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            // TODO : handle exception
            return;
        }
        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
            switch (dc.getType()) {
                case ADDED:
                    mValues.add(dc.getNewIndex(), dc.getDocument().toObject(DummyItem.class));
                    notifyItemInserted(dc.getNewIndex());
                    break;
                case MODIFIED:
                    if (dc.getOldIndex() == dc.getNewIndex()) {
                        mValues.set(dc.getOldIndex(), dc.getDocument().toObject(DummyItem.class));
                        notifyItemChanged(dc.getOldIndex());
                    } else {
                        mValues.remove(dc.getOldIndex());
                        mValues.add(dc.getNewIndex(), dc.getDocument().toObject(DummyItem.class));
                        notifyItemMoved(dc.getOldIndex(), dc.getNewIndex());
                    }
                case REMOVED:
                    mValues.remove(dc.getOldIndex());
                    notifyItemRemoved(dc.getOldIndex());
                    break;
                default:
                    break;
            }
        }



    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mUsername;
        public final TextView mPassword;
        public final TextView mNotes;
        public final Button delete;
        public final Button edit;
        public final Button copy;
        public final Button showPassword;

        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
            mUsername = (TextView) view.findViewById(R.id.username);
            mPassword = (TextView) view.findViewById(R.id.password);
            mNotes = (TextView) view.findViewById(R.id.notes);
            delete = (Button) view.findViewById(R.id.delete);
            edit = (Button) view.findViewById(R.id.edit);
            copy = (Button) view.findViewById(R.id.copy);
            showPassword = (Button)  view.findViewById(R.id.showPassword);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}

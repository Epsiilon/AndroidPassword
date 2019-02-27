package com.esgi.androidPassword;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ConnectionActivityInstrumentedTest {
    @Rule
    public ActivityTestRule<ConnectionActivity> activityTestRule = new ActivityTestRule<>(ConnectionActivity.class);

    @Test
    public void checkConnectionOK() {
        onView(withId(R.id.editText)).perform(replaceText("La sécurité9!"));
        onView(withId(R.id.editText)).perform(closeSoftKeyboard());

        onView(withId(R.id.validate)).perform(click());
    }

    @Test
    public void checkConnectionKO() {
        onView(withId(R.id.editText)).perform(replaceText("toto"));
        onView(withId(R.id.editText)).perform(closeSoftKeyboard());

        onView(withId(R.id.validate)).perform(click());

        onView(withId(R.id.validate)).check(matches(withText(R.string.validate)));
    }
}

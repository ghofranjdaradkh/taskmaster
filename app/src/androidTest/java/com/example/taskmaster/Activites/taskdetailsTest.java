package com.example.taskmaster.Activites;


import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import com.example.taskmaster.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class taskdetailsTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void taskdetailsTest() {
        ViewInteraction materialButton = onView(
allOf(withId(R.id.ADDTASK), withText("ADD TASK"),
childAtPosition(
childAtPosition(
withId(android.R.id.content),
0),
0),
isDisplayed()));
        materialButton.perform(click());
        
        ViewInteraction materialButton2 = onView(
allOf(withId(R.id.submitButton), withText("ADD TASK"),
childAtPosition(
childAtPosition(
withId(android.R.id.content),
0),
5),
isDisplayed()));
        materialButton2.perform(click());
        
        ViewInteraction recyclerView = onView(
allOf(withId(R.id.recyclerViewId),
childAtPosition(
withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
11)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));
        
        ViewInteraction appCompatImageView = onView(
allOf(withId(R.id.arrowImage),
childAtPosition(
childAtPosition(
withId(android.R.id.content),
0),
3),
isDisplayed()));
        appCompatImageView.perform(click());
        }
    
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup)parent).getChildAt(position));
            }
        };
    }
    }

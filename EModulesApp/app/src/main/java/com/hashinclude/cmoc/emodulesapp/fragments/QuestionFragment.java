package com.hashinclude.cmoc.emodulesapp.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hashinclude.cmoc.emodulesapp.utils.DatabaseAdapter;
import com.hashinclude.cmoc.emodulesapp.models.QuestionModel;
import com.hashinclude.cmoc.emodulesapp.R;
import com.hashinclude.cmoc.emodulesapp.eventListeners.SubmitButtonClickedEvent;

import org.greenrobot.eventbus.EventBus;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionFragment extends Fragment {

    EventBus bus;

    public QuestionFragment() {
        // Required empty public constructor
        bus = EventBus.getDefault();
    }

    public static QuestionFragment newInstance(QuestionModel questionModel) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("questionModel", questionModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View row = inflater.inflate(R.layout.fragment_question, container, false);
        final Bundle bundle = getArguments();
        final QuestionModel questionModel = bundle.getParcelable("questionModel");

        final DatabaseAdapter adapter = new DatabaseAdapter(getContext());

        final RadioRealButtonGroup answerButtonGroup = row.findViewById(R.id.answerRadioButtonGroup);
        WebView questionWebView = row.findViewById(R.id.questionWebView);
        final Button submitButton = row.findViewById(R.id.submitButton);
        TextView topicTextView = row.findViewById(R.id.topicOfQuestionTextView);

        final int[] optionSelected = {-1};
        final RadioRealButton[] chosenButton = new RadioRealButton[1];

        answerButtonGroup.setPosition(-1);


        topicTextView.setText("Topic : " + questionModel.getTopic());
        questionWebView.getSettings().setJavaScriptEnabled(true);
        questionWebView.loadDataWithBaseURL("", questionModel.getQuery(), "text/html", "UTF-8", "");
        questionWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
        questionWebView.setLongClickable(false);

        if (TextUtils.isEmpty(questionModel.getMarked())) {
            answerButtonGroup.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
                @Override
                public void onClickedButton(RadioRealButton button, int position) {
                    optionSelected[0] = position;
                    chosenButton[0] = button;
                }
            });


            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String chosenAnswer = "";
                    RadioRealButton[] correctButton;
                    switch (optionSelected[0]) {
                        case -1:
                            Toast.makeText(getContext(), "Select an option.", Toast.LENGTH_SHORT).show();
                            return;
                        case 0:
                            chosenAnswer = "A";
                            break;

                        case 1:
                            chosenAnswer = "B";
                            break;

                        case 2:
                            chosenAnswer = "C";
                            break;

                        case 3:
                            chosenAnswer = "D";
                            break;

                        case 4:
                            chosenAnswer = "E";
                            break;
                    }
                    answerButtonGroup.setClickable(false);
                    Toast.makeText(getContext(), "Solution Unlocked!", Toast.LENGTH_SHORT).show();
                    bus.post(new SubmitButtonClickedEvent(chosenAnswer));

                    if (chosenAnswer.equals(questionModel.getCorrect())) {
                        chosenButton[0].setBackgroundColor(Color.GREEN);
                    } else {
                        chosenButton[0].setBackgroundColor(Color.RED);
                        correctButton = currentChosenButton(row, questionModel.getCorrect());
                        correctButton[0].setBackgroundColor(Color.GREEN);
                    }
                    adapter.updateMarked(questionModel.getId(), chosenAnswer);
                    view.setOnClickListener(null);
                }
            });
        } else {
            answerButtonGroup.setClickable(false);
            submitButton.setClickable(false);
            if (questionModel.getMarked().equals(questionModel.getCorrect())) {
                //correct and marked button are same, so just put green on correct
                RadioRealButton[] buttonCorrect = currentChosenButton(row, questionModel.getCorrect());
                buttonCorrect[0].setBackgroundColor(Color.GREEN);
            } else {
                //correct and incorrect are different, so marked different
                RadioRealButton[] buttonMarked = currentChosenButton(row, questionModel.getMarked());
                RadioRealButton[] buttonCorrect = currentChosenButton(row, questionModel.getCorrect());
                buttonCorrect[0].setBackgroundColor(Color.GREEN);
                buttonMarked[0].setBackgroundColor(Color.RED);
            }
        }


        return row;
    }

    public RadioRealButton[] currentChosenButton(View row, String toCheck) {
        RadioRealButton[] correctButton = new RadioRealButton[1];
        switch (toCheck) {
            case "A":
                correctButton[0] = row.findViewById(R.id.radioButtonA);
                break;
            case "B":
                correctButton[0] = row.findViewById(R.id.radioButtonB);
                break;

            case "C":
                correctButton[0] = row.findViewById(R.id.radioButtonC);
                break;

//            case "D":
//                correctButton[0] = row.findViewById(R.id.radioButtonD);
//                break;
//
//            case "E":
//                correctButton[0] = row.findViewById(R.id.radioButtonE);
//                break;
        }
        return correctButton;
    }

}

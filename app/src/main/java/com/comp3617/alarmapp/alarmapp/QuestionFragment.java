package com.comp3617.alarmapp.alarmapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class QuestionFragment extends Fragment {
    private TextView question;
    private EditText answer;
    private boolean status;
    private TextWatcher textWatcher;

    public QuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_question, container, false);
    }

    public boolean getStatus() {
        return status;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String[] questions = getResources().getStringArray(R.array.questions);
        question = (TextView) view.findViewById(R.id.question);
        int rand = (int) (Math.random()*50);
        question.setText(questions[rand]);
        answer = (EditText) view.findViewById(R.id.answer);

        textWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!(question.getText().toString().equals(s.toString()))) {
                    answer.setError("Unmatching input. Please check your answer again.");
                    status = false;
                }
                else {
                    answer.setError(null);
                    status = true;
                }
            }
        };

        answer.addTextChangedListener(textWatcher);
    }


}

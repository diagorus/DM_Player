package com.dmplayer.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.butterknifeabstraction.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class InputDialog extends BaseDialogFragment {
    public static final String RESULT_INPUT = "input";

    private static final String ARG_TITLE = "title";
    private static final String ARG_INVITATION = "invitation";

    @BindView(R.id.text_invitation)
    TextView invitationText;
    @BindView(R.id.input)
    EditText input;
    @BindView(R.id.button_ok)
    Button buttonOk;
    @BindView(R.id.button_cancel)
    Button buttonCancel;

    public static InputDialog newInstance(String title, String invitation) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_INVITATION, invitation);

        InputDialog d = new InputDialog();
        d.setArguments(args);

        return d;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.dialog_input;
    }

    private void init() {
        Bundle args = getArguments();

        String title = args.getString(ARG_TITLE, "");
        String invitation = args.getString(ARG_INVITATION, "");

        getDialog().setTitle(title);
        invitationText.setText(invitation);

        input.setSelectAllOnFocus(true);
        input.requestFocus();
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
    }

    @OnClick(R.id.button_ok)
    public void finishAgree() {
        if (onWorkDoneWithResult != null) {
            Bundle result = new Bundle();
            result.putString(RESULT_INPUT, input.getText().toString());

            onWorkDoneWithResult.onAgree(result);
        }
        dismiss();
    }

    @OnClick(R.id.button_cancel)
    public void finishRefuse() {
        if (onWorkDoneWithResult != null) {
            onWorkDoneWithResult.onRefuse();
        }

        dismiss();
    }

    private OnWorkDoneWithResult onWorkDoneWithResult;
    public OnWorkDoneWithResult getOnWorkDoneWithResult() {
        return onWorkDoneWithResult;
    }
    public void setOnWorkDoneWithResult(OnWorkDoneWithResult OnWorkDone) {
        this.onWorkDoneWithResult = OnWorkDone;
    }
}
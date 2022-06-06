package android.cmcmanamon.tipcalculator;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TipFragment extends Fragment {
    public static final String TAG = "TipFragment";
    private static final String KEY_PERCENT = "percent";
    private static int MIN_TIP = 5;
    private static int MAX_TIP = 30;
    private SeekBar mTipSlider;
    private EditText mBillText, mTipAmountText, mTotalText;
    private Button mCalculateButton;
    private TextView mTipPercentText;
    private int tipPercent = 15;
    private double billAmount = 0;
    private double tipAmount = 0;
    private double totalAmount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tip, container, false);

        // restore previous percentage
        if(savedInstanceState != null) {
            tipPercent = savedInstanceState.getInt(KEY_PERCENT, 15);
        }

        // Slider
        mTipSlider = (SeekBar)v.findViewById(R.id.tipSlider);
        // Slider listener
        mTipSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser)
                    return;
                tipPercent = (progress + 1) * 5;
                updateTipUI();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Percent Text
        mTipPercentText = (TextView)v.findViewById(R.id.tipPercentText);


        // Bill Amount Text
        mBillText = (EditText)v.findViewById(R.id.billText);
        mBillText.setFilters(new InputFilter[]{new MoneyInputFilter(5, 2)});
        mBillText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                billAmount = dollarsDouble(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //  Tip Amount Text
        mTipAmountText = (EditText)v.findViewById(R.id.tipAmountText);

        // Total Text
        mTotalText = (EditText)v.findViewById(R.id.totalText);

        // Calculate Button
        mCalculateButton = (Button)v.findViewById(R.id.calculateButton);
        mCalculateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                updateValues();
            }
        });
        updateTipUI();
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_PERCENT, tipPercent);
    }

    public void updateValues() {
        tipAmount = billAmount * (double)tipPercent / 100;
        totalAmount = billAmount + tipAmount;
        updateValueUI();
    }

    public void updateValueUI() {
        mTipAmountText.setText(dollarsString(tipAmount));
        mTotalText.setText(dollarsString(totalAmount));
    }

    public void updateTipUI() {
        mTipPercentText.setText(tipPercent + " %");
        int sliderProgress = tipPercent / 5 - 1;
        mTipSlider.setProgress(sliderProgress);
    }

    // Helpers
    private static double dollarsDouble(String s) {
        double amt = 0.0;
        if (s == null)
            return amt;

        try {
            amt = Double.parseDouble(s);
            DecimalFormat currency = new DecimalFormat("#.##");
            return Double.valueOf(currency.format(amt));
        }
        catch (NumberFormatException e) {
            // Do nothing
        }

        return amt;
    }

    private static String dollarsString(double d) {
        return String.format("%.2f", d);
    }

    // used to restrict input format of money
    class MoneyInputFilter implements InputFilter {
        private Pattern pattern;

        MoneyInputFilter(int left, int right) {
            pattern = Pattern.compile("[0-9]{0," + (left - 1) + "}+((\\.[0-9]{0," + (right - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher matcher = pattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }
    }

}

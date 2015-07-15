package com.darqueflux.tipulator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    //Find a better place to put these...
    public final static int ROUNDING_STYLE_UP = 0;
    public final static int ROUNDING_STYLE_CLOSEST = 1;
    public final static int ROUNDING_STYLE_DOWN = 2;

    //Member variables
    protected Float billAmount = 0.0f;
    protected Float tipPercent = 0.0f;
    protected Float tipPercentMin = 10f;
    protected Float tipPercentMax = 50f;
    protected Float tipPercentResolution = 0.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get Data
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        tipPercent = preferences.getFloat("tip_percent", 18.0f);
        updateTipPercentRange();

        //Get Fields
        EditText txtBillAmount = (EditText)findViewById(R.id.txtBillAmount);
        SeekBar seekTipPercent = (SeekBar)findViewById(R.id.seekTipPercent);
        ImageButton btnTipPercentUp = (ImageButton)findViewById(R.id.btnTipPercentUp);
        ImageButton btnTipPercentDown = (ImageButton)findViewById(R.id.btnTipPercentDown);

        //Some Debug Stuff
        //Log.i("Tip Percent", String.format("%d", seekTipPercent.getProgress()));

        //Add Listeners
        txtBillAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                EditText txtBillAmount = (EditText) findViewById(R.id.txtBillAmount);
                billAmount = (txtBillAmount.getText().toString().length() > 0) ? ((txtBillAmount.getText().toString().equals(".")) ? (-1.0f) : (Float.valueOf(txtBillAmount.getText().toString()))) : (-1.0f);
                updateTipAmount();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        seekTipPercent.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SeekBar seekTipPercent = (SeekBar) findViewById(R.id.seekTipPercent);
                tipPercent = tipPercentMin + (seekTipPercent.getProgress() * tipPercentResolution);   //Change to determine with higher precision
                TextView txtTipPercent = (TextView) findViewById(R.id.txtTipPercent);
                txtTipPercent.setText(String.format("%.2f%%", tipPercent));
                updateTipAmount();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnTipPercentDown.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeekBar seekTipPercent = (SeekBar) findViewById(R.id.seekTipPercent);
                int progress = seekTipPercent.getProgress();
                if (progress > 0) {
                    seekTipPercent.setProgress(progress - 1);
                }
                /* Fun Functionality to remember
                Toast.makeText(MainActivity.this,
                        "ImageButton is clicked!", Toast.LENGTH_SHORT).show();
                */
            }
        });


        btnTipPercentUp.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeekBar seekTipPercent = (SeekBar) findViewById(R.id.seekTipPercent);
                int progress = seekTipPercent.getProgress();
                if(progress < seekTipPercent.getMax())
                {
                    seekTipPercent.setProgress(progress + 1);
                }
            }
        });

        /* REMOVED DUE TO ALTERED METHOD. Might cannibalize this in order to create the popup that changes it to a specified number. if i choose to do that.
        txtTipPercent.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                TextView txtTipPercent = (TextView) findViewById(R.id.txtTipPercent);
                tipPercent = (txtTipPercent.getText().toString().length() > 0)?(Float.valueOf(txtTipPercent.getText().toString())):(0);
                updateTipAmount();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });
        */
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        updateTipPercentRange();
        updateTipAmount();
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        prefEditor.putFloat("tip_percent", tipPercent);
        prefEditor.commit();
    }

    private void updateTipPercentRange() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String tipPercentMinString = preferences.getString("min_tip_percent", "10.0");
        tipPercentMin = (tipPercentMinString.length() > 0) ? ((tipPercentMinString.equals(".")) ? (10.0f) : (Float.valueOf(tipPercentMinString))) : (10.0f);
        String tipPercentMaxString = preferences.getString("max_tip_percent", "50.0");
        tipPercentMax = (tipPercentMaxString.length() > 0) ? ((tipPercentMaxString.equals(".")) ? (50.0f) : (Float.valueOf(tipPercentMaxString))) : (50.0f);
        String tipPercentResolutionString = preferences.getString("resolution_tip_percent", "0.5");
        tipPercentResolution = (tipPercentResolutionString.length() > 0) ? ((tipPercentResolutionString.equals(".")) ? (0.5f) : (Float.valueOf(tipPercentResolutionString))) : (0.5f);

        //Log.i("Tip Percent", String.format("Raw - Min : %s - Max : %s - Resolution %s", tipPercentMinString, tipPercentMaxString, tipPercentResolutionString));
        //Log.i("Tip Percent", String.format("Interpreted - Min : %.2f - Max : %.2f - Resolution %.4f - Tip Percent : %.2f", tipPercentMin, tipPercentMax, tipPercentResolution, tipPercent));

        if(tipPercent < tipPercentMin)
        {
            tipPercent = tipPercentMin;
        }
        else if(tipPercent > tipPercentMax)
        {
            tipPercent = tipPercentMax;
        }

        Integer seekProgress = Math.round((tipPercent - tipPercentMin) / tipPercentResolution);

        TextView txtTipPercent = (TextView)findViewById(R.id.txtTipPercent);
        SeekBar seekTipPercent = (SeekBar)findViewById(R.id.seekTipPercent);

        txtTipPercent.setText(String.format("%.2f%%", tipPercent));
        seekTipPercent.setMax(Math.round((tipPercentMax - tipPercentMin) / tipPercentResolution));
        seekTipPercent.setProgress(Math.min(seekTipPercent.getMax(), Math.max(0, seekProgress)));

        //Log.i("Tip Percent", String.format("tip : %.2f - min : %.2f - Reso : %.2f - %d", tipPercent, tipPercentMin, tipPercentResolution, Math.round((tipPercent - tipPercentMin) / tipPercentResolution)));
    }

    protected void updateTipAmount()
    {
        EditText txtBillAmount = (EditText) findViewById(R.id.txtBillAmount);
        TextView txtTipPercent = (TextView) findViewById(R.id.txtTipPercent);

        //Show dashes if there is no bill amount
        if (txtBillAmount.getText().toString().length() <= 0 || txtBillAmount.getText().toString().equals("."))
        {
            TextView txtTipAmount = (TextView) findViewById(R.id.txtTipAmount);
            txtTipAmount.setText("---");

            TextView txtTotalAmount = (TextView) findViewById(R.id.txtTotalAmount);
            txtTotalAmount.setText("---");

            return;
        }

        Float tipTotal = billAmount * (tipPercent / 100);

        //Apply SecuriTip
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String securityModeString = preferences.getString("security_mode", "0");
        Integer securityMode = (securityModeString.length() > 0) ? (Integer.valueOf(securityModeString)) : (0);  //TODO: Make "security_mode" a constant somewhere
        String roundingStyleString = preferences.getString("rounding_style", "0");
        Integer roundingStyle = (roundingStyleString.length() > 0) ? (Integer.valueOf(roundingStyleString)) : (0);  //TODO: Make "rounding_style" a constant somewhere
        Integer extraChange;
        switch(securityMode)    //TODO: Update the switch case to use constants instead of number or preprocessor defines.
        {
            case 1: //Constant Change
                Integer changeAmount = Integer.valueOf(preferences.getString("constant_change_amount", "0")); //TODO: make "constant_change_amount" a constant

                Integer bigTipSubTotal = Math.round(tipTotal * 100);
                extraChange = changeAmount - ((bigTipSubTotal + Math.round(billAmount * 100)) % 100);
                switch(roundingStyle)
                {
                    case ROUNDING_STYLE_CLOSEST:
                        if(extraChange <= -50)
                        {
                            extraChange += 100;
                        }
                        else if(extraChange > 50)
                        {
                            extraChange -= 100;
                        }
                        break;
                    case ROUNDING_STYLE_DOWN:
                        if(extraChange < 0)
                        {
                            if (extraChange + bigTipSubTotal < 0) {
                                extraChange += 100;
                            }
                        }
                        else if(extraChange > 0)
                        {
                            if(extraChange+bigTipSubTotal - 100 >= 0)
                            {
                                extraChange -= 100;
                            }
                        }
                        break;
                    default:
                    case ROUNDING_STYLE_UP:
                        if(extraChange < 0)
                        {
                            extraChange += 100;
                        }
                        break;
                }
                tipTotal = (bigTipSubTotal + extraChange) / 100.0f;
                break;
            case 2:
                //TODO: Optimize this
                //Log.i("CheckSum", String.format("Starting With : %.5f", (tipTotal + billAmount) * 100));
                Integer l = (int) Math.floor(Math.log10(Math.round((tipTotal + billAmount) * 100))) + 1;
                Integer target = (int) Math.pow(10, Math.ceil(l / 2.0f));
                //Log.i("CheckSum", String.format("l : %d -- target : %d" , l, target));
                Float temp = (float)Math.round((tipTotal + billAmount) * 100) / target;
                Integer rightSide = (int) Math.round((temp % 1) * target);
                Integer leftSide = (int) Math.floor(temp);
                //Log.i("CheckSum", String.format("Temp : %.5f -- Left : %d -- Right : %d", temp , leftSide, rightSide));
                extraChange = target - (rightSide + leftSide);
                Integer newL;
                switch(roundingStyle)
                {
                    case ROUNDING_STYLE_CLOSEST:
                        if(extraChange > 50)
                        {
                            extraChange -= target - 1;
                        }
                        else if(extraChange <= -50)
                        {
                            extraChange += target - 1;
                        }
                        break;
                    case ROUNDING_STYLE_DOWN:
                        if(extraChange > 0)
                        {
                            extraChange -= target - 1;
                        }
                        break;
                    case ROUNDING_STYLE_UP:
                    default:
                        if(extraChange < 0)
                        {
                            extraChange += target - 1;
                        }
                        //Log.i("CheckSum", String.format("tipTotal : %.2f -- extraChange(Not Added Yet) : %d", tipTotal, extraChange));
                        break;
                }
                tipTotal += extraChange / 100.0f;
                newL = (int) Math.floor(Math.log10(Math.round((tipTotal + billAmount) * 100))) + 1;
                //Log.i("CheckSum", String.format("tipTotal : %.2f -- newAmount : %.2f -- l : %d -- newL : %d", tipTotal, (float)Math.round((tipTotal + billAmount) * 100), l, newL));
                if(Math.ceil(newL/2.0f) != Math.ceil(l/2.0f))
                {
                    switch(roundingStyle)
                    {
                        case ROUNDING_STYLE_CLOSEST:    //Shares the down because I don't know what I want it to do.
                        case ROUNDING_STYLE_DOWN:
                            if (tipTotal - ((target - 1) / 100.0f) < 0)   //Gotta go big.
                            {
                                tipTotal += ((target * 10) - (target / 10)) / 100.0f;
                            } else {    //Go Down
                                tipTotal -= ((target - 1) / 100.0f);
                            }
                            break;
                        case ROUNDING_STYLE_UP:
                        default:
                            tipTotal += ((target * 10) - (target / 10)) / 100.0f;   //Go Big, All the time!
                            break;
                    }
                }
                break;
            default:
                break;
        }

        //Limit tip total to 2 digits.
        tipTotal = (float)Math.round(tipTotal * 100.0f) / 100f;

        //Display the values
        TextView txtTipAmount = (TextView) findViewById(R.id.txtTipAmount);
        txtTipAmount.setText(String.format("%.2f (%.2f%%)", tipTotal, (tipTotal / billAmount) * 100.0f));

        TextView txtTotalAmount = (TextView) findViewById(R.id.txtTotalAmount);
        txtTotalAmount.setText(String.format("%.2f", tipTotal+billAmount));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

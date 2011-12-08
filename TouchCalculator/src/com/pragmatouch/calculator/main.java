package com.pragmatouch.calculator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Stack;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class main extends Activity {	
	GridView mKeypadGrid;
	TextView userInputText;
	TextView memoryStatText;

	Stack<String> mInputStack;
	Stack<String> mOperationStack;

	KeypadAdapter mKeypadAdapter;
	TextView mStackText;
	boolean resetInput = false;
	boolean hasFinalResult = false;

	String mDecimalSeperator;
	double memoryValue = Double.NaN;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DecimalFormat currencyFormatter = (DecimalFormat) NumberFormat
				.getInstance();
		char decimalSeperator = currencyFormatter.getDecimalFormatSymbols()
				.getDecimalSeparator();
		mDecimalSeperator = Character.toString(decimalSeperator);

		setContentView(R.layout.main);

		// Create the stack
		mInputStack = new Stack<String>();
		mOperationStack = new Stack<String>();

		// Get reference to the keypad button GridView
		mKeypadGrid = (GridView) findViewById(R.id.grdButtons);

		// Get reference to the user input TextView
		userInputText = (TextView) findViewById(R.id.txtInput);
		userInputText.setText("0");

		memoryStatText = (TextView) findViewById(R.id.txtMemory);
		memoryStatText.setText("");

		mStackText = (TextView) findViewById(R.id.txtStack);
		
		// screen fix
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);		

		// Create Keypad Adapter
		mKeypadAdapter = new KeypadAdapter(this);

		// Set adapter of the keypad grid
		mKeypadGrid.setAdapter(mKeypadAdapter);
		
		// Set button click listener of the keypad adapter
		mKeypadAdapter.setOnButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Button btn = (Button) v;
				// Get the KeypadButton value which is used to identify the
				// keypad button from the Button's tag
				KeypadButton keypadButton = (KeypadButton) btn.getTag();

				// Process keypad button
				ProcessKeypadInput(keypadButton);
			}
		});

		mKeypadGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

			}
		});
	}

	private void ProcessKeypadInput(KeypadButton keypadButton) {
		//Toast.makeText(this, keypadButton.getText(), Toast.LENGTH_SHORT).show();
		String text = keypadButton.getText().toString();
		String currentInput = userInputText.getText().toString();
		
		int currentInputLen = currentInput.length();
		String evalResult = null;
		double userInputValue = Double.NaN;
		
		Log.i("jdebug", keypadButton + "");
		switch (keypadButton) {
		case BACKSPACE: // Handle backspace
			// If has operand skip backspace
			if (resetInput)
				return;

			int endIndex = currentInputLen - 1;

			// There is one character at input so reset input to 0
			if (endIndex < 1) {
				userInputText.setText("0");
			}
			// Trim last character of the input text
			else {
				userInputText.setText(currentInput.subSequence(0, endIndex));
			}
			break;
		case SIGN: // Handle -/+ sign
			// input has text and is different than initial value 0
			if (currentInputLen > 0 && currentInput != "0") {
				// Already has (-) sign. Remove that sign
				if (currentInput.charAt(0) == '-') {
					userInputText.setText(currentInput.subSequence(1,
							currentInputLen));
				}
				// Prepend (-) sign
				else {
					userInputText.setText("-" + currentInput.toString());
				}
			}
			break;
		case CE: // Handle clear input
			userInputText.setText("0");
			break;
		case C: // Handle clear input and stack
			userInputText.setText("0");
			clearStacks();
			break;
		case DECIMAL_SEP: // Handle decimal seperator
			if (hasFinalResult || resetInput) {
				userInputText.setText("0" + mDecimalSeperator);
				hasFinalResult = false;
				resetInput = false;
			} else if (currentInput.contains("."))
				return;
			else
				userInputText.append(mDecimalSeperator);
			break;
		case DIV:
		case PLUS:
		case MINUS:
		case MULTIPLY:
			if (resetInput) {
				mInputStack.pop();
				mOperationStack.pop();
			} else {
				if (currentInput.charAt(0) == '-') {
					mInputStack.add("(" + currentInput + ")");
				} else {
					mInputStack.add(currentInput);
				}
				mOperationStack.add(currentInput);
			}

			mInputStack.add(text);
			mOperationStack.add(text);

			dumpInputStack();
			evalResult = evaluateResult(false);
			if (evalResult != null)
				userInputText.setText(evalResult);

			resetInput = true;
			break;
		case CALCULATE:
			if (mOperationStack.size() == 0)
				break;

			mOperationStack.add(currentInput);
			evalResult = evaluateResult(true);
			if (evalResult != null) {
				clearStacks();
				userInputText.setText(evalResult);
				resetInput = false;
				hasFinalResult = true;
			}
			break;
		case M_ADD: // Add user input value to memory buffer
			userInputValue = tryParseUserInput();
			if (Double.isNaN(userInputValue))
				return;
			if (Double.isNaN(memoryValue))
				memoryValue = 0;
			memoryValue += userInputValue;
			displayMemoryStat();

			hasFinalResult = true;

			break;
		case M_REMOVE: // Subtract user input value to memory buffer
			userInputValue = tryParseUserInput();
			if (Double.isNaN(userInputValue))
				return;
			if (Double.isNaN(memoryValue))
				memoryValue = 0;
			memoryValue -= userInputValue;
			displayMemoryStat();
			hasFinalResult = true;
			break;
		case MC: // Reset memory buffer to 0
			memoryValue = Double.NaN;
			displayMemoryStat();
			break;
		case MR: // Read memoryBuffer value
			if (Double.isNaN(memoryValue))
				return;
			userInputText.setText(doubleToString(memoryValue));
			displayMemoryStat();
			break;
		case MS: // Set memoryBuffer value to user input
			userInputValue = tryParseUserInput();
			if (Double.isNaN(userInputValue))
				return;
			memoryValue = userInputValue;
			displayMemoryStat();
			hasFinalResult = true;
			break;
			
		case PERCENT:
			{	
				//passwd.dat 파일이 없으면 만든다.
				try {
					FileInputStream fis = openFileInput("passwd.dat");
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					
					FileOutputStream fos;
					try {
						
						fos = openFileOutput("passwd.dat", Context.MODE_WORLD_READABLE);
						String str = "1004";
			        	fos.write(str.getBytes());
			        	fos.close();
			        	
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				String strPasswd = null;
				try {
					FileInputStream fis = openFileInput("passwd.dat");
					byte[] data = new byte[fis.available()];
					while(fis.read(data) != -1){;}
					fis.close();
					strPasswd = new String(data);
					
					if(strPasswd.equals("") == true)
					{
						strPasswd = "1004";
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(userInputText.getText().toString().equals(strPasswd) == true)
				{	
					Intent i = new Intent(main.this, SecretManager.class);
					startActivity(i);
					userInputText.setText("");
				}
				else
					userInputText.setText("");
			}				
			break;
		
		default:
			if (Character.isDigit(text.charAt(0))) {
				if (currentInput.equals("0") || resetInput || hasFinalResult) {
					userInputText.setText(text);
					resetInput = false;
					hasFinalResult = false;
				} else {
					userInputText.append(text);
					resetInput = false;
				}

			}
			break;

		}

	}

	private void clearStacks() {
		mInputStack.clear();
		mOperationStack.clear();
		mStackText.setText("");
	}

	private void dumpInputStack() {
		Iterator<String> it = mInputStack.iterator();
		StringBuilder sb = new StringBuilder();

		while (it.hasNext()) {
			CharSequence iValue = it.next();
			sb.append(iValue);

		}

		mStackText.setText(sb.toString());
	}

	private String evaluateResult(boolean requestedByUser) {
		if ((!requestedByUser && mOperationStack.size() != 4)
				|| (requestedByUser && mOperationStack.size() != 3))
			return null;

		String left = mOperationStack.get(0);
		String operator = mOperationStack.get(1);
		String right = mOperationStack.get(2);
		String tmp = null;
		if (!requestedByUser)
			tmp = mOperationStack.get(3);

		double leftVal = Double.parseDouble(left.toString());
		double rightVal = Double.parseDouble(right.toString());
		double result = Double.NaN;

		if (operator.equals(KeypadButton.DIV.getText())) {
			result = leftVal / rightVal;
		} else if (operator.equals(KeypadButton.MULTIPLY.getText())) {
			result = leftVal * rightVal;

		} else if (operator.equals(KeypadButton.PLUS.getText())) {
			result = leftVal + rightVal;
		} else if (operator.equals(KeypadButton.MINUS.getText())) {
			result = leftVal - rightVal;

		}

		String resultStr = doubleToString(result);
		if (resultStr == null)
			return null;

		mOperationStack.clear();
		if (!requestedByUser) {
			mOperationStack.add(resultStr);
			mOperationStack.add(tmp);
		}

		return resultStr;
	}

	private String doubleToString(double value) {
		if (Double.isNaN(value))
			return null;

		long longVal = (long) value;
		if (longVal == value)
			return Long.toString(longVal);
		else
			return Double.toString(value);

	}

	private double tryParseUserInput() {
		String inputStr = userInputText.getText().toString();
		double result = Double.NaN;
		try {
			result = Double.parseDouble(inputStr);

		} catch (NumberFormatException nfe) {
		}
		return result;

	}

	private void displayMemoryStat() {
		if (Double.isNaN(memoryValue)) {
			memoryStatText.setText("");
		} else {
			memoryStatText.setText("M = " + doubleToString(memoryValue));
		}
	}

}

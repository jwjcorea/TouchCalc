package com.pragmatouch.calculator;

public enum KeypadButton {
	MC("MC",KeypadButtonCategory.MEMORYBUFFER)
	, MR("MR",KeypadButtonCategory.MEMORYBUFFER)
	, MS("MS",KeypadButtonCategory.MEMORYBUFFER)
	, M_ADD("M+",KeypadButtonCategory.MEMORYBUFFER)
	, M_REMOVE("M-",KeypadButtonCategory.MEMORYBUFFER)
	, BACKSPACE("<-",KeypadButtonCategory.CLEAR)
	, CE("CE",KeypadButtonCategory.CLEAR)
	, C("C",KeypadButtonCategory.CLEAR)
	, ZERO("0",KeypadButtonCategory.NUMBER)
	, ONE("1",KeypadButtonCategory.NUMBER)
	, TWO("2",KeypadButtonCategory.NUMBER)
	, THREE("3",KeypadButtonCategory.NUMBER)
	, FOUR("4",KeypadButtonCategory.NUMBER)
	, FIVE("5",KeypadButtonCategory.NUMBER)
	, SIX("6",KeypadButtonCategory.NUMBER)
	, SEVEN("7",KeypadButtonCategory.NUMBER)
	, EIGHT("8",KeypadButtonCategory.NUMBER)
	, NINE("9",KeypadButtonCategory.NUMBER)
	, PLUS(" + ",KeypadButtonCategory.OPERATOR)
	, MINUS(" - ",KeypadButtonCategory.OPERATOR)
	, MULTIPLY(" * ",KeypadButtonCategory.OPERATOR)
	, DIV(" / ",KeypadButtonCategory.OPERATOR)
	, RECIPROC("1/x",KeypadButtonCategory.OTHER)
	, DECIMAL_SEP(",",KeypadButtonCategory.OTHER)
	, SIGN("+-",KeypadButtonCategory.OTHER)
	, SQRT("sqrt",KeypadButtonCategory.OTHER)
	, PERCENT("%",KeypadButtonCategory.OTHER)
	, CALCULATE("=",KeypadButtonCategory.RESULT)
	, DUMMY("",KeypadButtonCategory.DUMMY);

	CharSequence mText; // Display Text
	KeypadButtonCategory mCategory;
	
	KeypadButton(CharSequence text,KeypadButtonCategory category) {
		mText = text;
		mCategory = category;
	}

	public CharSequence getText() {
		return mText;
	}
}

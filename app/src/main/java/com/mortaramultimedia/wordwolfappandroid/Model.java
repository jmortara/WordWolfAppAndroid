package com.mortaramultimedia.wordwolfappandroid;

import java.util.ArrayList;
import java.util.HashMap;

public class Model
{
	public static Boolean DEBUG_MODE = false;
	public static int rows = -1;
	public static int cols = -1;
	public static String letterSet = "abcdefghijklmnopqrstuvwxyz";
	public static ArrayList<Character> letterSetArray;
	public static Character[][] boardData;
	public static ArrayList<PositionObj> moves;
	public static ArrayList<String> foundWords;
	public static HashMap<String, String> globalDictionary;
}

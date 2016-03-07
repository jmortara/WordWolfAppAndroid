package com.mortaramultimedia.wordwolfappandroid.unused;

public class PositionObj {
	public int col = -1;
	public int row = -1;

	public PositionObj(int col, int row)
	{
		this.col = col;
		this.row = row;
	}

	@Override
	public String toString()
	{
		return "col " + col + " row " + row;
	}
}

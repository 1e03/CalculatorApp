package sakashita.leo.jp.calculatorapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	private TextView textView;
	private Button button;
	private static final int SWIPE_MIN_DISTANCE = 50;
	private static final int SWIPE_THRSHOLD_VELOCITY = 200;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	
	private int recentOperator = R.id.result_btn;
	private double result = 0;
	private boolean isOperatorPushed = false;
	private String strtmp = "";
	private int inttmp = 0;
	private boolean priorityCalcFlag = false;
	private String btnmsg = "";
	private int stackCnt = 0;
	private Button clrbtn = null;
	private boolean periodFlag = false;
	private boolean inputflag = false;
	private boolean zeroFlag = false;
	private boolean divideFlag = false;
	private boolean zeroDivideFlag = false;
	
	//入力値を入れるスタック
	Stack<String> stack_op;
	Stack<String> stack_num;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Log.d(TAG, "onCreate");
		
		textView = findViewById(R.id.textView);
		
		clrbtn = findViewById(R.id.clear_btn);
		
		stack_op = new Stack<>();
		stack_num = new Stack<>();
		
		//数字キー
		findViewById(R.id.button0).setOnClickListener(buttonNumberListener);
		findViewById(R.id.button1).setOnClickListener(buttonNumberListener);
		findViewById(R.id.button2).setOnClickListener(buttonNumberListener);
		findViewById(R.id.button3).setOnClickListener(buttonNumberListener);
		findViewById(R.id.button4).setOnClickListener(buttonNumberListener);
		findViewById(R.id.button5).setOnClickListener(buttonNumberListener);
		findViewById(R.id.button6).setOnClickListener(buttonNumberListener);
		findViewById(R.id.button7).setOnClickListener(buttonNumberListener);
		findViewById(R.id.button8).setOnClickListener(buttonNumberListener);
		findViewById(R.id.button9).setOnClickListener(buttonNumberListener);
		findViewById(R.id.period_btn).setOnClickListener(buttonNumberListener);
		
		//計算キー
		findViewById(R.id.per_btn).setOnClickListener(buttonOperatorListener);
		findViewById(R.id.divide_btn).setOnClickListener(buttonOperatorListener);
		findViewById(R.id.multiply_btn).setOnClickListener(buttonOperatorListener);
		findViewById(R.id.add_btn).setOnClickListener(buttonOperatorListener);
		findViewById(R.id.sub_btn).setOnClickListener(buttonOperatorListener);
		findViewById(R.id.result_btn).setOnClickListener(buttonOperatorListener);
		
		//クリア、マイナスキー
		findViewById(R.id.clear_btn).setOnClickListener(buttonListener);
		findViewById(R.id.invert_btn).setOnClickListener(buttonListener);
		
	}
	
	//フリック処理
	private GestureDetector.SimpleOnGestureListener OnGestureListener = new GestureDetector.SimpleOnGestureListener() {
		
		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
			
			try {
				float distance_x = Math.abs((event1.getX() - event2.getX()));
				float velocity_x = Math.abs(velocityX);
				Log.d(TAG, "distance_x:" + distance_x + ", velocity_x:" + velocity_x);
				
				if (Math.abs(event1.getY() - event2.getY()) > SWIPE_MAX_OFF_PATH) {
					Log.d(TAG, "縦移動距離大");
				}else if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRSHOLD_VELOCITY) {
					Log.d(TAG, "右から左");
					if (textView.getText() == "0") {
						inputflag = false;
					}else {
						strtmp = textView.getText().toString();
						inttmp = strtmp.length();
						if (inttmp >= 2) {
							strtmp = strtmp.substring(0, inttmp-1);
						}else {
							strtmp = "0";
							inputflag = false;
						}
						textView.setText(strtmp);
					}
				}else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRSHOLD_VELOCITY) {
					Log.d(TAG, "左から右");
					if (textView.getText() == "0") {
						inputflag = false;
					}else {
						strtmp = textView.getText().toString();
						inttmp = strtmp.length();
						if (inttmp >= 2) {
							strtmp = strtmp.substring(0, inttmp-1);
						}else {
							strtmp = "0";
							inputflag = false;
						}
						textView.setText(strtmp);
					}
				}
			}catch (Exception e) {
			}
			return false;
		}
	};
	
	//数字入力処理
	View.OnClickListener buttonNumberListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			button = (Button)v;
			clrbtn.setText("C");
			if (inputflag) {
				if (isOperatorPushed) {
					zeroFlag = false;
					if (v.getId() == R.id.period_btn) {
						strtmp = "0" + button.getText().toString();
						Log.d(TAG, "strtmp:" + strtmp);
						textView.setText(strtmp);
						periodFlag = true;
					}else {
						if (v.getId() == R.id.button0) {
							if (divideFlag) {
								zeroDivideFlag = true;
							}else {}
							zeroFlag = true;
						}else {}
						strtmp = button.getText().toString();
						result = Double.parseDouble(strtmp);
						textView.setText((format(result)));
					}
					isOperatorPushed = false;
				}else {
					if (periodFlag && v.getId() == R.id.period_btn) {
					}else {
						if (v.getId() == R.id.button0) {
							if (zeroFlag) {
							}else {
								strtmp = textView.getText().toString();
								strtmp += button.getText().toString();
								if (strtmp.length() >= 9) {
									strtmp = strtmp.substring(0,8);
								}else {}
								textView.setText(strtmp);
								result = Double.parseDouble(textView.getText().toString());
							}
						}else {
							if (zeroFlag) {
								if (v.getId() == R.id.period_btn) {
									strtmp = "0" + button.getText().toString();
								}else {
									strtmp = button.getText().toString();
								}
							}else {
								strtmp = textView.getText().toString();
								Log.d(TAG, "strtmp:" + strtmp);
								strtmp += button.getText().toString();
								Log.d(TAG, "strtmp:" + strtmp);
							}
							if (strtmp.length() >= 9) {
								strtmp = strtmp.substring(0,8);
							}else {}
							textView.setText(strtmp);
							result = Double.parseDouble(textView.getText().toString());
							zeroFlag = false;
						}
					}
				}
			}else {
				if (v.getId() == R.id.period_btn) {
					strtmp = textView.getText().toString();
					Log.d(TAG, "strtmp:" + strtmp);
					strtmp += button.getText().toString();
					Log.d(TAG, "strtmp:" + strtmp);
					periodFlag = true;
				}else if (v.getId() == R.id.button0){
					if (divideFlag) {
						zeroDivideFlag = true;
					}else {}
					zeroFlag = true;
					strtmp = button.getText().toString();
				}else {
					strtmp = button.getText().toString();
				}
				textView.setText(strtmp);
				inputflag = true;
				isOperatorPushed = false;
			}
		}
	};
	
	private double calculate(int operator, double value1, double value2) {
		switch (operator) {
			case R.id.add_btn:
				return value1 + value2;
			case R.id.sub_btn:
				return value1 - value2;
			case R.id.divide_btn:
				return value1 / value2;
			case R.id.multiply_btn:
				return value1 * value2;
			default:
				return value1;
		}
	}
	
	//小数点以下のゼロを消すメソッド
	String format(double d) {
		if (d == (long)d) {
			return String.format("%d", (long)d);
		}else {
			return String.format("%s", d);
		}
	}
	
	//演算子押下処理
	View.OnClickListener buttonOperatorListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Button operatorBtn = (Button)v;
			double value1,value2;
			recentOperator = operatorBtn.getId();
			switch (recentOperator) {
				case R.id.result_btn:
					btnmsg = "=が押されました。";
					break;
				case R.id.divide_btn:
					btnmsg = "÷が押されました。";
					divideFlag = true;
					break;
				case R.id.multiply_btn:
					btnmsg = "×が押されました。";
					break;
				case R.id.sub_btn:
					btnmsg = "-が押されました。";
					break;
				case R.id.add_btn:
					btnmsg = "＋が押されました。";
					break;
			}
			Log.d(TAG, "onClickButtonId:" + btnmsg);
			
			stack_num.push(textView.getText().toString());
			if (recentOperator == R.id.result_btn) {
				if (stack_op.size() == 0) {
					result = Double.parseDouble(textView.getText().toString());
					textView.setText(format(result));
				}else {
					stackCnt = stack_op.size();
					if (zeroDivideFlag) {
						textView.setText("エラー");
					}else {
						for (int i=0;i<stackCnt;i++) {
							value2 = Double.parseDouble(stack_num.pop());
							value1 = Double.parseDouble(stack_num.pop());
							recentOperator = Integer.parseInt(stack_op.pop());
							result = calculate(recentOperator, value1, value2);
							stack_num.push(String.valueOf(result));
							Log.d(TAG, i + "回目の計算");
							Log.d(TAG, format(result));
						}
						result = Double.parseDouble(stack_num.pop());
						if (String.valueOf(result).length() >= 9) {
							textView.setText(format(result).substring(0,8));
						}else {
							textView.setText(format(result));
						}
					}
				}
				inputflag = false;
				priorityCalcFlag = false;
				isOperatorPushed = false;
			}else {
				if (recentOperator == R.id.multiply_btn || recentOperator == R.id.divide_btn) {
					if (priorityCalcFlag) {
						if (zeroDivideFlag) {
							textView.setText("エラー");
						}else {
							inttmp = recentOperator;
							value2 = Double.parseDouble(stack_num.pop());
							Log.d(TAG, "value2:" + value2);
							value1 = Double.parseDouble(stack_num.pop());
							Log.d(TAG, "value1:" + value1);
							recentOperator = Integer.parseInt(stack_op.pop());
							result = calculate(recentOperator, value1, value2);
							stack_num.push(String.valueOf(result));
							result = Double.parseDouble(stack_num.peek());
							if (String.valueOf(result).length() > 9) {
								textView.setText(format(result).substring(0,8));
							}else {
								textView.setText(format(result));
							}
							recentOperator = inttmp;
						}
					}else {
					}
					priorityCalcFlag = true;
				}else if (recentOperator == R.id.per_btn){
					value1 = Double.parseDouble(stack_num.pop()) /100;
					if (priorityCalcFlag) {
						stack_num.push(String.valueOf(value1));
						result = Double.parseDouble(stack_num.peek());
						textView.setText(format(result));
					}else {
						value2 = Double.parseDouble(stack_num.peek());
						result = value1 * value2;
						stack_num.push(String.valueOf(result));
						result = Double.parseDouble(stack_num.peek());
						textView.setText(format(result));
					}
				}else {
					if (priorityCalcFlag) {
						if (zeroDivideFlag) {
							textView.setText("エラー");
						}else {
							inttmp = recentOperator;
							stackCnt = stack_op.size();
							for (int i=0;i<stackCnt;i++) {
								value2 = Double.parseDouble(stack_num.pop());
								value1 = Double.parseDouble(stack_num.pop());
								recentOperator = Integer.parseInt(stack_op.pop());
								result = calculate(recentOperator, value1, value2);
								stack_num.push(String.valueOf(result));
								result = Double.parseDouble(stack_num.peek());
								if (String.valueOf(result).length() > 9) {
									textView.setText(format(result).substring(0,8));
								}else {
									textView.setText(format(result));
								}
							}
							recentOperator = inttmp;
							Log.d(TAG, "inttmp:" + inttmp);
						}
					}else {
						stackCnt = stack_num.size();
						Log.d(TAG, "stackCnt:" + stackCnt);
						inttmp = recentOperator;
						if (stackCnt > 1) {
							value2 = Double.parseDouble(stack_num.pop());
							value1 = Double.parseDouble(stack_num.pop());
							if (stack_op.size() == 0) {
							}else{
								recentOperator = Integer.parseInt(stack_op.pop());
								Log.d(TAG, "recentOperator:" + recentOperator);
							}
							result = calculate(recentOperator, value1, value2);
							stack_num.push(String.valueOf(result));
							result = Double.parseDouble(stack_num.peek());
							recentOperator = inttmp;
							if (String.valueOf(result).length() > 9) {
								textView.setText(format(result).substring(0,8));
							}else {
								textView.setText(format(result));
							}
						}else {
						}
					}
					priorityCalcFlag = false;
				}
				
				stack_op.push(String.valueOf(recentOperator));
				//stack_op.push(button.getText().toString());
				
				isOperatorPushed = true;
			}
			periodFlag = false;
			zeroFlag = false;
		}
	};
	
	//クリア、プラスとマイナス変換
	View.OnClickListener buttonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.clear_btn:
					clrbtn.setText("AC");
					double doubletmp = 0;
					if (zeroDivideFlag) {
					}else {
						doubletmp = Double.parseDouble(textView.getText().toString());
					}
					if (doubletmp == 0) {
						isOperatorPushed = false;
						stackCnt = stack_op.size();
						for (int i=0;i<stack_op.size();i++) {
							stack_op.pop();
						}
						stackCnt = stack_num.size();
						Log.d(TAG, "stackCnt:" + stackCnt);
						for (int i=0;i<stack_num.size();i++) {
							stack_num.pop();
						}
					}else {
						result = 0;
						textView.setText(format(result));
					}
					result = 0;
					inputflag = false;
					priorityCalcFlag = false;
					zeroFlag = false;
					periodFlag = false;
					divideFlag = false;
					zeroDivideFlag = false;
					textView.setText(format(result));
					btnmsg = "クリアボタンが押されました。";
					break;
				case R.id.invert_btn:
					result = Double.parseDouble(textView.getText().toString());
					if (result >= 0) {
						result = -result;
						textView.setText(format(result));
					}else {
						result = Math.abs(result);
						textView.setText(format(result));
					}
					btnmsg = "変換ボタンが押されました。";
					break;
			}
			Log.d(TAG, "buttonListener:" + btnmsg);
		}
	};
}

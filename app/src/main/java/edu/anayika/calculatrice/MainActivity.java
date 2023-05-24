package edu.anayika.calculatrice;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.fathzer.soft.javaluator.DoubleEvaluator;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView equation;
    TextView result;
    String userInput = "";
    String bufferInput = "";
    boolean endCompute = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        equation = findViewById(R.id.equation);
        result = findViewById(R.id.result);

        ArrayList<View> buttonsLayout = (findViewById(R.id.buttons_layout)).getTouchables();
        for(View button: buttonsLayout) {
            int buttonId = button.getId();
            String textButton = ((Button)findViewById(buttonId)).getText().toString();

            switch (textButton) {
                case "C":
                    button.setOnClickListener(view -> clearTextViews());
                    break;
                case "1/x":
                    button.setOnClickListener(view -> showOneOnX());
                    break;
                case "x²":
                    button.setOnClickListener(view -> showSquare());
                    break;
                case "←":
                    button.setOnClickListener(view -> clearOne());
                    break;
                case "√":
                    button.setOnClickListener(view -> showSquareRoot());
                    break;
                case "+/−":
                    button.setOnClickListener(view -> toggleMinus());
                    break;
                case "=":
                    button.setOnClickListener(view -> calculate());
                    break;
                default:
                    button.setOnClickListener(view -> showUserInput(textButton));
            }
        }
    }

    private boolean verifyInput(String newInput) {
        boolean isValid = true;
        switch (newInput) {
            case "×": case "+": case "−": case "÷": case ",":
                if(userInput.equals("")) {
                    isValid = false;
                } else {
                    String previousChar = userInput.substring(userInput.length() - 1);
                    if (!previousChar.matches("-?\\d+(\\.\\d+)?") && !previousChar.equals(")") && !previousChar.equals("²")) {
                        isValid = false;
                    }
                }
                break;
        }
        return isValid;
    }

    private void showUserInput(String newInput) {
        if(verifyInput(newInput)) {
            if (endCompute) {
                endCompute = false;
                bufferInput = "";

                switch (newInput) {
                    case "×":
                    case "+":
                    case "−":
                    case "÷":
                        userInput = result.getText().toString() + newInput;
                        break;
                    case ",":
                        userInput = "0,";
                        result.setText("");
                        break;
                    default:
                        bufferInput += newInput;
                        userInput = newInput;
                        result.setText(userInput);
                }
            } else {
                if (newInput.equals(",")) {
                    if (!userInput.equals("") && userInput.substring(userInput.length() - 1).matches("-?\\d+(\\.\\d+)?")) {
                        userInput += newInput;
                    } else {
                        userInput += "0,";
                    }
                    bufferInput += newInput;
                    result.setText(userInput);
                } else {
                    userInput += newInput;
                    if (newInput.matches("-?\\d+(\\.\\d+)?")) {
                        bufferInput += newInput;
                        result.setText(bufferInput);
                    } else {
                        bufferInput = "";
                        result.setText(userInput);
                    }
                }
            }
            equation.setText(userInput);
        }
    }

    private void clearTextViews() {
        equation.setText("");
        result.setText("0");
        userInput = "";
        bufferInput = "";
    }

    private void showOneOnX() {
        userInput += "^(-1)";
        equation.setText(userInput);
    }

    private void showSquare() {
        userInput += "²";
        equation.setText(userInput);
    }

    private void clearOne() {
        if(!endCompute && equation.getText().length() > 0) {
            equation.setText(equation.getText().toString().substring(0, (equation.getText().length()) - 1));
            result.setText(result.getText().toString().substring(0, (result.getText().length()) - 1));
            userInput = userInput.substring(0, userInput.length() - 1);
        } else {
            clearTextViews();
        }
    }

    private void showSquareRoot() {
        userInput += "^0.5";
        equation.setText(userInput);
    }

    private void toggleMinus() {
        if(userInput.equals("")) {
            userInput = "-";
            bufferInput = "-";
        } else {
            int number = userInput.lastIndexOf(bufferInput);
            if(bufferInput.startsWith("-")) {
                bufferInput = bufferInput.substring(1);
            } else {
                bufferInput = "-" + bufferInput;
            }
            userInput = userInput.substring(0, number) + bufferInput;
        }
        result.setText(userInput);
        equation.setText(userInput);
    }

    private void calculate() {
        DoubleEvaluator eval = new DoubleEvaluator();

        String equal = userInput + "=";
        equation.setText(equal);

        String mathString = userInput
                .replaceAll("×", "*")
                .replaceAll("\\+", "+")
                .replaceAll("−", "-")
                .replaceAll("÷", "\\/")
                .replaceAll("²", "^2")
                .replaceAll(",",".");

        Double res = eval.evaluate(mathString);

        if(res.isInfinite() || res.isNaN()) {
            userInput = "";
            bufferInput = "";
            equation.setText(R.string.error_message);
        } else {
            DecimalFormat df = new DecimalFormat("#.#######");
            String formattedResult = df.format(res);
            formattedResult = formattedResult.replaceAll("\\.", ",");

            result.setText(formattedResult);
        }

        endCompute = true;
    }
}
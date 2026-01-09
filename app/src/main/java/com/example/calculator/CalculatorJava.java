package com.example.calculator;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayDeque;

public class CalculatorJava extends AppCompatActivity {

    private TextView display;
    private String current = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        display = findViewById(R.id.display);

        String[][] buttons = {
                {"7", "8", "9", "÷"},
                {"4", "5", "6", "×"},
                {"1", "2", "3", "-"},
                {"C", "0", "=", "+"}
        };

        LinearLayout container = (LinearLayout) findViewById(R.id.container);

        for (String[] row : buttons) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setWeightSum(4);

            for (String label : row) {
                TextView btn = new TextView(this);
                btn.setText(label);
                btn.setTextSize(24f);
                btn.setTextColor(0xFFFFFFFF);
                btn.setGravity(android.view.Gravity.CENTER);
                btn.setBackgroundColor(0xFF333333);
                btn.setPadding(20, 20, 20, 20);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );
                params.setMargins(8, 8, 8, 8);

                btn.setLayoutParams(params);
                btn.setOnClickListener(v -> {
                    current = onButtonClick(label, current);
                    display.setText(current.isEmpty() ? "0" : current);
                });

                rowLayout.addView(btn);
            }

            container.addView(rowLayout);
        }
    }

    private String onButtonClick(String value, String disp) {
        if (value.equals("C")) return "";
        if (value.equals("=")) return formatResult(evaluate(disp));
        return disp + value;
    }


    private double evaluate(String expr) {
        ArrayDeque<Double> values = new ArrayDeque<>();
        ArrayDeque<Character> ops = new ArrayDeque<>();

        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);

            if (Character.isDigit(c)) {
                StringBuilder num = new StringBuilder();
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    num.append(expr.charAt(i));
                    i++;
                }
                values.addLast(Double.parseDouble(num.toString()));
                continue;
            }

            if (c == '+' || c == '-' || c == '×' || c == '÷') {
                while (!ops.isEmpty() && precedence(ops.peekLast()) >= precedence(c)) {
                    double res = apply(values.removeLast(), values.removeLast(), ops.removeLast());
                    values.addLast(res);
                }
                ops.addLast(c);
            }

            i++;
        }

        while (!ops.isEmpty()) {
            double res = apply(values.removeLast(), values.removeLast(), ops.removeLast());
            values.addLast(res);
        }

        return values.peekLast();
    }

    private int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '×' || op == '÷') return 2;
        return 0;
    }

    private double apply(double b, double a, char op) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '×': return a * b;
            case '÷': return b != 0 ? a / b : Double.NaN;
        }
        return 0;
    }

    private String formatResult(double r) {
        if (r % 1 == 0) return String.valueOf((int) r);
        return String.valueOf(r);
    }
}

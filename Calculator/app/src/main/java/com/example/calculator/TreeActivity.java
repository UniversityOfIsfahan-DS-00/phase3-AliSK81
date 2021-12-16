package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import de.blox.treeview.BaseTreeAdapter;
import de.blox.treeview.TreeView;

public class TreeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);

        TreeView treeView = findViewById(R.id.treeView);

        BaseTreeAdapter<ViewHolder> adapter = new BaseTreeAdapter<ViewHolder>(this, R.layout.tree_view_node) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(View view) {
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(ViewHolder viewHolder, Object data, int position) {
                viewHolder.textView.setText(data.toString());

            }
        };

        treeView.setAdapter(adapter);

        String input = getIntent().getStringExtra("input");

        Calculator calc = new Calculator();


        try {
            adapter.setRootNode(calc.toExpressionTree(input).getRoot());

        } catch (RuntimeException ex) {
            Toast.makeText(this, "Check Input", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}

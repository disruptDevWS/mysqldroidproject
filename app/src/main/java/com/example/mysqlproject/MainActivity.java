package com.example.mysqlproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

	ItemAdapter itemAdapter;
	Context thisContext;
	ListView myListView;
	TextView progressTextView;
	Map<String, Double> fruitsMap = new LinkedHashMap<String, Double>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Resources res = getResources();
		myListView = (ListView) findViewById(R.id.myListView);
		progressTextView = (TextView) findViewById(R.id.progressTextView);
		thisContext = this;

		progressTextView.setText("");
		Button btn = (Button) findViewById(R.id.getDataButton);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GetData retrieveData = new GetData();
				retrieveData.execute("");
			}
		});
	}

	private class GetData extends AsyncTask<String, String, String> {

		String msg = "";

		static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		static final String DB_URL = "jdbc.mysql://" +
						DbStrings.DATABASE_URL + "/" +
						DbStrings.DATABASE_NAME;

		@Override
		protected void onPreExecute() {
			progressTextView.setText("Connecting to database...");
		}

		@Override
		protected String doInBackground(String... params) {

			java.sql.Connection conn = null;
			java.sql.Statement stmt = null;

			try {
					Class.forName(JDBC_DRIVER);
					conn = DriverManager.getConnection(DB_URL, DbStrings.USERNAME, DbStrings.PASSWORD);

					stmt = conn.createStatement();
					String sql = "SELECT * FROM fruits";
					ResultSet rs = stmt.executeQuery(sql);

					while (rs.next()) {
						String name = rs.getString("name");
						double price = rs.getDouble("price");

						fruitsMap.put(name, price);
					}

					msg = "Process complete.";

					rs.close();
					stmt.close();
					conn.close();

			} catch (SQLException connError) {
				msg = "An exception was thrown for JDBC.";
				connError.printStackTrace();
			} catch (ClassNotFoundException e) {
				msg = "A class not found exception was thrown.";
				e.printStackTrace();
			} finally {

				try {
					if (stmt != null) {
						stmt.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				try {
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

			return null;
		}

		@Override
		protected void onPostExecute(String msg) {

			progressTextView.setText(this.msg);

			if(fruitsMap.size() > 0) {

				itemAdapter = new ItemAdapter(thisContext, fruitsMap);
				myListView.setAdapter(itemAdapter);

			}

		}

	}

} //end MainActivity

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * 
 * @author Vincenzo
 *
 */

public class Controller { 
	public static void main(String[] args) throws Exception {
		System.out.println(EmployeeDAO.selectAllEmployees()); // print out the employee list (makes adding new ones easier as you can see the next id available)
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0); // put the server on localhost:8000
		server.createContext("/json", new HttpHandler() { // make an page appear when localhost:8000/json is entered
			@Override
			public void handle(HttpExchange he) throws IOException {
				ArrayList<Employee> responseArray = null; // array to store employee objects
				try {
					responseArray = EmployeeDAO.selectAllEmployees(); // put the employees in the array
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Gson gson = new Gson();
				String myJson = gson.toJson(responseArray); // put the response array in json
				Controller.writeResponse(he, myJson); // write the json data
			}
		});
		server.createContext("/new", new HttpHandler() { // page for the "/new" suffix
			@Override
			public void handle(HttpExchange he) throws IOException {
				// output HTML form
				he.sendResponseHeaders(200, 0);
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));
				out.write("<html><head></head><body><form method=\"POST\" action=\"/added\">"); // begin form, upon form submission send to /added page
				out.write("ID:<input name=\"id\"><br>"); // data to be entered to create a new employee
				out.write("Name:<input name=\"name\"><br>");
				out.write("Gender:<input name=\"gender\"><br>");
				out.write("DOB:<input name=\"dob\"><br>");
				out.write("Address:<input name=\"address\"><br>");
				out.write("Postcode:<input name=\"postcode\"><br>");
				out.write("NIN:<input name=\"nin\"><br>");
				out.write("Job Title:<input name=\"title\"><br>");
				out.write("Start Date:<input name=\"start\"><br>");
				out.write("Salary:<input name=\"salary\"><br>");
				out.write("Email:<input name=\"email\"><br>");
				out.write("<input type=\"submit\" value=\"Submit\">"); // add the button
				out.write("</form></body></html>"); // close the tags
				out.close();
			}
		});
		server.createContext("/added", new HttpHandler() { // page for the "/added" suffix
			// process data from the form
			@Override
			public void handle(HttpExchange he) throws IOException {
				HashMap<String, String> post = new HashMap<String, String>();
				// read the request body
				BufferedReader in = new BufferedReader(new InputStreamReader(he.getRequestBody()));
				String line = "";
				String request = "";
				while ((line = in.readLine()) != null) {
					request = request + line;
				}
				// individual key value pairs are delimited by ampersands.
				String[] pairs = request.split("&");
				for (int i = 0; i < pairs.length; i++) {
					// decode each key value pair
					String pair = pairs[i];
					post.put(URLDecoder.decode(pair.split("=")[0], "UTF-8"),
							URLDecoder.decode(pair.split("=")[1], "UTF-8"));
				}
				// get all the variables put into local strings
				String id = (post.get("id"));
				String name = (post.get("name"));
				String gender = (post.get("gender"));
				String dob = (post.get("dob"));
				String address = (post.get("address"));
				String postcode = (post.get("postcode"));
				String nin = (post.get("nin"));
				String title = (post.get("title"));
				String start = (post.get("start"));
				String salary = (post.get("salary"));
				String email = (post.get("email"));
				//create an employee object using the strings of data
				Employee e1 = new Employee(id,name,gender,dob,address,postcode,nin,title,start,salary,email);
				try {
					EmployeeDAO.insertEmployee(e1); // add the employee into the database
				} catch (ClassNotFoundException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));
				out.write("Employee added");
				out.write("<html><head></head><body><form method=\"POST\" action=\"/json\">"); // upon submission send to /json
				out.write("<input type=\"submit\" value=\"Return\">"); // button for submission
				out.write("</form></body></html>"); // close tags
				he.sendResponseHeaders(200, 0); // HTTP 200 (OK)
				out.close();
			}
		});
		server.setExecutor(null); // creates a default executor
		server.start();
		System.out.println("The server is up and running on port 8000");
	}

	public static void writeResponse(HttpExchange httpExchange, String response) throws IOException {
		httpExchange.sendResponseHeaders(200, response.length());
		OutputStream os = httpExchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

}
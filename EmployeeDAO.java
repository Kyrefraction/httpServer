import java.sql.*;
import java.util.ArrayList;

public class EmployeeDAO {

	public static ArrayList<Employee> selectAllEmployees()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		Class.forName("org.sqlite.JDBC");
		ArrayList<Employee> employeeList = new ArrayList<Employee>();
		try {

			String dbURL = "jdbc:sqlite:empdb.sqlite";
			con = DriverManager.getConnection(dbURL);
			st = con.createStatement();
			rs = st.executeQuery("SELECT * FROM employees;");
			while (rs.next()) {
				Employee E = new Employee(  rs.getString("ID"), rs.getString("Name"), rs.getString("Gender"),
						rs.getString("DOB"), rs.getString("Address"), rs.getString("Postcode"), rs.getString("NIN"),
						rs.getString("jobTitle"), rs.getString("StartDate"), rs.getString("Salary"),
						rs.getString("Email"));
				employeeList.add(E);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}  finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return employeeList;
	}

	public static void insertEmployee(Employee employee) throws ClassNotFoundException, SQLException {
		Connection con = null;
		Statement st = null;
		String breaker = "', '";
		Class.forName("org.sqlite.JDBC");
		try {
			String dbURL = "jdbc:sqlite:empdb.sqlite";
			con = DriverManager.getConnection(dbURL);
			st = con.createStatement();
			st.executeUpdate("INSERT INTO employees (ID, Name, Gender, DOB, Address, Postcode, NIN, jobTitle, StartDate, Salary, Email)"
					+ " VALUES (" + "'" + employee.getId() + breaker +  employee.getName() + breaker +  employee.getGender() + breaker
					+ employee.getDob() + breaker + employee.getAddress() + breaker + employee.getPostcode() + breaker
					+ employee.getNatInscNo() + breaker + employee.getTitle() + breaker + employee.getStartDate() + breaker
					+ employee.getSalary() + breaker + employee.getEmail() + "');");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}  finally {
			try {
				if (st != null) {
					st.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		System.out.println("Employee successfully created");
	}

	public static void deleteEmployeeById(int identified) {
		Connection connection = null;
		Statement statement = null;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:empdb.sqlite");
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			String sql = "DELETE from employees where ID = " + identified + ";";
			statement.executeUpdate(sql);
			connection.commit();
			statement.close();
			connection.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Delete operation successfully done");
	}
}

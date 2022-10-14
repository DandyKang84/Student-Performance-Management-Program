package studentProject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBConnection {
	private Connection connection = null;
	private Statement statement = null;
	//ResultSet 쿼리문실행해서 레코드 값을 저장(set방식저장)하는 하나의 객체
	private ResultSet rs = null;

	// Connection
	public void connect() {
		// ======================properties======================
		// properties db.properties load
		// 1. Properties
		Properties properties = new Properties();
		// 2. properties 파일을 로드
		FileInputStream fis = null;
		try {

			fis = new FileInputStream("C:/JAVA_TEST/studentProject/src/studentProject/db.properties");
			properties.load(fis);
		} catch (FileNotFoundException e) {
			System.out.println("FileInputStream Error" + e.getMessage());
		} catch (IOException e) {
			System.out.println("Properties Error" + e.getMessage());
		}
		// ======================================================
		// ======================Driver load=====================
		// 데이타베이스 연결
		try {
			// 드라이버 로드 "com.mysql.cj.jdbc.Driver"
			Class.forName(properties.getProperty("driver"));
			// 데이타베이스 접속요청
			connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("userid"),
					properties.getProperty("password"));
		} catch (ClassNotFoundException e) {
			System.out.println("Class.forName load Error" + e.getMessage());
		} catch (SQLException e) {
			System.out.println("Connection Error" + e.getMessage());
		}
		// ======================================================
	}

	// insert Statement
	public int insert(Student student) {
		PreparedStatement ps = null;
		int insertReturnValue = -1;
//		String insertQuery = "insert into student(no,name,kor,eng,math,total,avr,grade)" + " values(?,?,?,?,?,?,?,?)";
		String insertQuery = "call procedure_insert_student( ?, ?, ?, ?, ?)";
		try {
			
			ps = connection.prepareStatement(insertQuery); // 준비
			ps.setString(1, student.getNo());
			ps.setString(2, student.getName());
			ps.setInt(3, student.getKor());
			ps.setInt(4, student.getEng());
			ps.setInt(5, student.getMath());
//			ps.setInt(6, student.getTotal());
//			ps.setDouble(7, student.getAvr());
//			ps.setString(8, student.getGrade());
			// 삽입성공하면 리턴값 1 : 삽입 블럭지정하고 번개를 클릭한다.
			insertReturnValue = ps.executeUpdate(); // 실행버튼 (번개)
		} catch (SQLException e) {
			System.out.println("InsertReturnValue Error" + e.getMessage());
		} catch (Exception e) {
			System.out.println("Exception Error" + e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PreparedStatement Close Error" + e.getMessage());
			}
		}
		return insertReturnValue;
	}

	// delete statement
	public int delete(String no) {
		PreparedStatement ps = null;
		int deleteReturnValue = -1;
		String deleteQuery = "delete from student where no = ?";
		try {
			ps = connection.prepareStatement(deleteQuery); // 준비
			ps.setString(1, no);

			// 삭제성공하면 리턴값 1
			deleteReturnValue = ps.executeUpdate(); // 실행버튼 (번개)
		} catch (Exception e) {
			System.out.println("Delete Error" + e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PreparedStatement Close Error" + e.getMessage());
			}
		}
		return deleteReturnValue;
	}

	// select statement
	public List<Student> select() {
		List<Student> list = new ArrayList<Student>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String selectQuery = "select * from student";
		try {
			ps = connection.prepareStatement(selectQuery); // 준비
			// select 성공하면 리턴값 ResultSet, 오류면 null
			// ps.exeuteUpdate(query), ps.executeQuery(query)
			// update, Query 구분 잘해서 사용해야 함.
			rs = ps.executeQuery(selectQuery); // 실행버튼 (번개)
			// 결과값이 없을때 체크하는 방법
			// isBeforeDirst(): 
			// 결과값이 RecordSet에 저장되어 있는데 레코드 단위로 저장되어있음
			// 그 첫번째 레코드 읽기 위해서 커서가 위치해 있는지 물어본다.
			if (!(rs != null || rs.isBeforeFirst())) {
				return list;
			}
			// rs.next() : 현재 커서에 있는 레코드 위치로 간다. 있으면(준비되있으면) true
			while (rs.next()) {
				String no = rs.getString("no");
				String name = rs.getString("name");
				int kor = rs.getInt("kor");
				int eng = rs.getInt("eng");
				int math = rs.getInt("math");
				int total = rs.getInt("total");
				double avr = rs.getDouble("avr");
				String grade = rs.getString("grade");
				int rate = rs.getInt("rate");
				list.add(new Student(no, name, kor, eng, math, total, avr, grade, rate));
			}

		} catch (Exception e) {
			System.out.println("Select Error" + e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PreparedStatement Close Error" + e.getMessage());
			}
		}
		return list;
	}
	
	// selectSerach statement
	public List<Student> selectSerach(String data, int type) {
		final int TYPEONE = 1, TYPETWO = 2;
		List<Student> list = new ArrayList<Student>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String selectSearchQuery = "select * from student where";
		try {
			switch(type) {
			case TYPEONE : 
				selectSearchQuery += " no like ? ";
				break;
			case TYPETWO :
				selectSearchQuery += " name like ? ";
				break;
			default : System.out.println("잘못된 입력 타입");
				return list;
			}
			ps = connection.prepareStatement(selectSearchQuery); // 준비
			// select 성공하면 리턴값 ResultSet, 오류면 null
			// ps.exeuteUpdate(query), ps.executeQuery(query)
			// update, Query 구분 잘해서 사용해야 함.
			//String namePattern = "%" + data + "%";
			ps.setString(1, "%" + data + "%"); // % -> 와일드카드
			rs = ps.executeQuery(); // 실행버튼 (번개)

			// 결과값이 없을때 체크하는 방법
			if (!(rs != null || rs.isBeforeFirst())) {
				return list;
			}
			// rs.next() : 현재 커서에 있는 레코드 위치로 간다.
			while (rs.next()) {
				String no = rs.getString("no");
				String name = rs.getString("name");
				int kor = rs.getInt("kor");
				int eng = rs.getInt("eng");
				int math = rs.getInt("math");
				int total = rs.getInt("total");
				double avr = rs.getDouble("avr");
				String grade = rs.getString("grade");
				int rate = rs.getInt("rate");
				list.add(new Student(no, name, kor, eng, math, total, avr, grade, rate));
			}

		} catch (Exception e) {
			System.out.println("SelectSearch Error" + e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PreparedStatement Close Error" + e.getMessage());
			}
		}
		return list;
	}
	// select 필드 from 테이블 where 조건 grop by 필드명 having order by limit 100
	// Update statement
	public int update(Student student) {
		PreparedStatement ps = null;
		int updateReturnValue = -1;
		String insertQuery = "UPDATE student SET kor = ?, eng = ?, math = ?,"
				+ "total = ?, avr = ?, grade = ? where no = ?";
		try {
			ps = connection.prepareStatement(insertQuery); // 준비
			ps.setInt(1, student.getKor());
			ps.setInt(2, student.getEng());
			ps.setInt(3, student.getMath());
			ps.setInt(4, student.getTotal());
			ps.setDouble(5, student.getAvr());
			ps.setString(6, student.getGrade());
			ps.setString(7, student.getNo());
			// 삽입성공하면 리턴값 1 : 삽입 블럭지정하고 번개를 클릭한다.
			updateReturnValue = ps.executeUpdate(); // 실행버튼 (번개)
		} catch (SQLException e) {
			System.out.println("updateReturnValue Error" + e.getMessage());
		} catch (Exception e) {
			System.out.println("Exception Error" + e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PreparedStatement Close Error" + e.getMessage());
			}
		}
		return updateReturnValue;
	
	}
	
	// selectOrderBy statement
	public List<Student> selectOrderBy(int type) {
		final int TYPEONE = 1, TYPETWO = 2, TYPETRHEE = 3;
		List<Student> list = new ArrayList<Student>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String selectOrderByQuery = "select * from student order by ";
		try {
			switch(type) {
			case TYPEONE : selectOrderByQuery += "no asc "; break;
			case TYPETWO : selectOrderByQuery += "name asc "; break;
			case TYPETRHEE : selectOrderByQuery += "total desc "; break;
			default: System.out.println("정렬타입 오류"); 
				return list;
			}
			ps = connection.prepareStatement(selectOrderByQuery); // 준비
			// select 성공하면 리턴값 ResultSet, 오류면 null
			// ps.exeuteUpdate(query), ps.executeQuery(query)
			// update, Query 구분 잘해서 사용해야 함.
			rs = ps.executeQuery(); // 실행버튼 (번개)
			// 결과값이 없을때 체크하는 방법
			if (!(rs != null || rs.isBeforeFirst())) {
				return list;
			}
			// rs.next() : 현재 커서에 있는 레코드 위치로 간다.
			int rank = 0;
			while (rs.next()) {
				String no = rs.getString("no");
				String name = rs.getString("name");
				int kor = rs.getInt("kor");
				int eng = rs.getInt("eng");
				int math = rs.getInt("math");
				int total = rs.getInt("total");
				double avr = rs.getDouble("avr");
				String grade = rs.getString("grade");
				int rate = rs.getInt("rate");
				//순위가 정해지지않아서 임시로 토탈값에 순위를 넣기위해 사용
				if(type == 3) {
					rate = ++rank; // 나중에 데이타베이스 rate 업데이트 해주면 된다.
				}
				list.add(new Student(no, name, kor, eng, math, total, avr, grade, rate));
			}

		} catch (Exception e) {
			System.out.println("Select Error" + e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PreparedStatement Close Error" + e.getMessage());
			}
		}
		return list;
	}
	// select Max, Min statement
	public List<Student> selectMaxMin(int type) {
		final int TYPEONE = 1, TYPETWO = 2;
		List<Student> list = new ArrayList<Student>();
		Statement statement = null;
		ResultSet rs = null;
		//서브쿼리를 구현해서 최대값과 같은 레크드정보를 보여주기 위함
		String selectMaxMinQuery = "select * from student where total = ";
		try {
			switch(type) {
			case TYPEONE : selectMaxMinQuery += "(select max(total) from student)"; break;
			case TYPETWO : selectMaxMinQuery += "(select min(total) from student)"; break;
			default: System.out.println("Max,Min 타입 오류"); 
				return list;
			}
			statement = connection.createStatement(); // 준비
			// select 성공하면 리턴값 ResultSet, 오류면 null
			// statement.exeuteUpdate(query), statement.executeQuery(query)
			// update, Query 구분 잘해서 사용해야 함.
			rs = statement.executeQuery(selectMaxMinQuery); // 실행버튼 (번개)
			// 결과값이 없을때 체크하는 방법
			if (!(rs != null || rs.isBeforeFirst())) {
				return list;
			}
			// rs.next() : 현재 커서에 있는 레코드 위치로 간다.
			
			while (rs.next()) {
				String no = rs.getString("no");
				String name = rs.getString("name");
				int kor = rs.getInt("kor");
				int eng = rs.getInt("eng");
				int math = rs.getInt("math");
				int total = rs.getInt("total");
				double avr = rs.getDouble("avr");
				String grade = rs.getString("grade");
				int rate = rs.getInt("rate");
				
				list.add(new Student(no, name, kor, eng, math, total, avr, grade, rate));
			}

		} catch (Exception e) {
			System.out.println("Select Max, Min Error" + e.getMessage());
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				System.out.println("PreparedStatement Close Error" + e.getMessage());
			}
		}
		return list;
		
	}
	
	// Connection Close
	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			System.out.println("Statement or ResultSet Close Error" + e.getMessage());
		}
	}

	

	

	

}
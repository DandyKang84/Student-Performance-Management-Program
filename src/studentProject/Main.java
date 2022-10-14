package studentProject;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
	public static Scanner sc = new Scanner(System.in);
	public static final int INPUT = 1, UPDATE = 2, DELETE = 3, SEARCH = 4, OUTPUT = 5;
	public static final int SORT = 6, STATS = 7, EXIT = 8;

	public static void main(String[] args) {
		DBConnection dbConn = new DBConnection();
		// 데이타베이스 연결
		dbConn.connect();
		// 메뉴선택
		boolean loopFlag = false;
		while (!loopFlag) {
			// 메뉴입력
			int num = displayMenu();
			switch (num) {
			case INPUT:
				studentInputData();
				break;
			case UPDATE:
				studentUpDate();
				break;
			case DELETE:
				studentDelete();
				break;
			case SEARCH:
				studentSearch();
				break;
			case OUTPUT:
				studentOutput();
				break;
			case SORT:
				studentSort();
				break;
			case STATS:
				studentStats();
				break;
			case EXIT:
				System.out.println("프로그램이 종료 됩니다. Have a nice day~~");
				loopFlag = true;
				break;
			default:
				System.out.println("숫자 1 ~ 8번까지 다시 입력바랍니다.");
				break;
			}
		}
		System.out.println("프로그램 종료");
	}

	// 학생정보 통계
	private static void studentStats() {

		List<Student> list = new ArrayList<Student>();
		try {
			// 데이타베이스 연결, 학생이름 검색
			// 1. class.forname(), mysql lord 2.connect(url,id,pass)
			// 3. statement, PreparedStatement  4. executeQuery,executeUpdate
			// DBConnection이 1,2번까지의 역할
			DBConnection dbConn = new DBConnection();
			// 데이타베이스 연결
			dbConn.connect();
			System.out.print("1.최고 점수 조회,  2.최저 점수 조회 >>>>>");
			int type = sc.nextInt();
			boolean value = checkInputPattern(String.valueOf(type), 5);
			if (!value)
				return;
			//드라이버로드커넥션 리드 execuQuery 쿼리문
			list = dbConn.selectMaxMin(type);
			if (list.size() <= 0) {
				System.out.println("검색한 학생정보가 없습니다." + list.size());
				return;
			}
			for (Student student : list) {
				System.out.println(student);
			}
			dbConn.close();
		} catch (InputMismatchException e) {
			System.out.println("입력타입이 맞지 않습니다. 재입력 바랍니다." + e.getMessage());
			return;
		} catch (Exception e) {
			System.out.println("데이타베이스 통계에러" + e.getMessage());
		}
	}

	// 학생정보 정렬: 학번, 이름, 총점
	private static void studentSort() {
		List<Student> list = new ArrayList<Student>();
		try {
			DBConnection dbConn = new DBConnection();
			dbConn.connect();
			System.out.print("정렬기준 선택 -> 1:학생 번호 2:이  름 3:총 점 : ");
			int type = sc.nextInt();
			// 번호 패턴검색
			boolean value = checkInputPattern(String.valueOf(type), 4);
			if (!value)
				return;
			list = dbConn.selectOrderBy(type);
			if (list.size() <= 0) {
				System.out.println("보여줄 리스트가 없습니다." + list.size());
				return;
			}
			// 리스트 내용 출력
			for (Student student : list) {
				System.out.println(student);
			}
			dbConn.close();
		} catch (Exception e) {
			System.out.println("데이타베이스 정렬 에러" + e.getMessage());
		}
		return;
	}

	// 학생정보 수정(점수 수정)
	private static void studentUpDate() {
		List<Student> list = new ArrayList<Student>();
		// 수정할 학생번호 입력
		System.out.println("수정할 학생번호 입력하시오");
		String no = sc.nextLine();
		// 번호 패턴검색
		boolean value = checkInputPattern(no, 1);
		if (!value)
			return;
		// 번호로 검색해서 불러내야 한다.
		DBConnection dbConn = new DBConnection();
		// 데이타베이스 연결
		dbConn.connect();
		// 테이블 데이터 입력
		list = dbConn.selectSerach(no, 1);
		if (list.size() <= 0) {
			System.out.println("검색한 학생정보가 없습니다." + list.size());
			return;
		}
		for (Student student : list) {
			System.out.println(student);
		}
		// 수정할 리스트를 보여줘야 된다.
		// 국,영,수 점수 재입력
		Student imsiStudent = list.get(0);
		System.out.print("국어" + imsiStudent.getKor() + ">>");
		int kor = sc.nextInt();
		value = checkInputPattern(String.valueOf(kor), 3);
		if (!value)
			return;
		imsiStudent.setKor(kor);

		System.out.print("영어" + imsiStudent.getEng() + ">>");
		int eng = sc.nextInt();
		value = checkInputPattern(String.valueOf(eng), 3);
		if (!value)
			return;
		imsiStudent.setEng(eng);

		System.out.print("수학" + imsiStudent.getMath() + ">>");
		int math = sc.nextInt();
		value = checkInputPattern(String.valueOf(math), 3);
		if (!value)
			return;
		imsiStudent.setMath(math);

		// 총합, 평균, 등급
		imsiStudent.calTotal();
		imsiStudent.calAvr();
		imsiStudent.calGrade();
		// 데이타베이스 수정할부분을 update 진행
		int returnUpdataValue = dbConn.update(imsiStudent);
		if (returnUpdataValue == -1) {
			System.out.println("수정할정보가 없습니다." + returnUpdataValue);
			return;
		}
		System.out.println("학생정보가 수정되었습니다." + returnUpdataValue);
		dbConn.close();
	}

	// 학생정보 검색(이름)
	private static void studentSearch() {
		List<Student> list = new ArrayList<Student>();
		try {
			// 검색할 학생번호 입력
			System.out.print("검색할 이름 입력: ");
			String name = sc.nextLine();
			// 패턴검색
			boolean value = checkInputPattern(name, 2);
			if (!value)
				return;
			// 데이타베이스 연결, 학생이름 검색
			DBConnection dbConn = new DBConnection();
			// 데이타베이스 연결
			dbConn.connect();
			// 테이블 데이터 입력
			list = dbConn.selectSerach(name, 2);
			if (list.size() <= 0) {
				System.out.println("검색한 학생정보가 없습니다." + list.size());
				return;
			}
			for (Student student : list) {
				System.out.println(student);
			}
			dbConn.close();
		} catch (InputMismatchException e) {
			System.out.println("입력타입이 맞지 않습니다. 재입력 바랍니다." + e.getMessage());
			return;
		} catch (Exception e) {
			System.out.println("데이타베이스 삭제에러" + e.getMessage());
		}
	}

	// 학생정보 전체출력
	private static void studentOutput() {
		List<Student> list = new ArrayList<Student>();
		try {
			// 데이타베이스 연결
			DBConnection dbConn = new DBConnection();
			dbConn.connect();
			list = dbConn.select();
			if (list.size() <= 0) {
				System.out.println("보여줄 리스트가 없습니다." + list.size());
				return;
			}
			// 리스트 내용 출력
			for (Student student : list) {
				System.out.println(student);
			}
			dbConn.close();
		} catch (Exception e) {
			System.out.println("데이타베이스 보여주기 에러" + e.getMessage());
		}
		return;
	}

	// 학생정보 삭제
	private static void studentDelete() {
		try {
			// 삭제할 학생번호 입력
			System.out.print("삭제할 학생번호 입력 (예:010101) >>>");
			String no = sc.nextLine();
			// no 문자열 패턴 검색
			boolean value = checkInputPattern(no, 1);
			if (!value)
				return;
			DBConnection dbConn = new DBConnection();
			// 데이타베이스 연결
			dbConn.connect();
			// 테이블 데이터 입력
			int deleteReturnValue = dbConn.delete(no);
			if (deleteReturnValue == -1) {
				System.out.println("삭제 [실패]입니다!" + deleteReturnValue);
			}
			if (deleteReturnValue == 0) {
				System.out.println("삭제 할 번호가 없습니다" + deleteReturnValue);
			} else {
				System.out.println("삭제 [성공]입니다!" + "리턴값: " + deleteReturnValue);
			}
			dbConn.close();
		} catch (InputMismatchException e) {
			System.out.println("입력타입이 맞지 않습니다. 재입력 바랍니다." + e.getMessage());
			return;
		} catch (Exception e) {
			System.out.println("데이타베이스 삭제에러" + e.getMessage());
		} 
	}

	// 학생정보 입력 -> 데이터베이스 연결 insert
	private static void studentInputData() {
		String pattern = null;
		boolean regex = false;
		try {
			// 학년(1~3학년:01,02,03)반(1~9:01~09)번호(01~60)
			System.out.print("학년(01,02,03)반(01~09)번호(01~60): ");
			String no = sc.nextLine();
			// no 문자열 패턴 검색
			boolean value = checkInputPattern(no, 1);
			if (!value) return;

			System.out.print("이름을 입력하세요 >>>>>> ");
			String name = sc.nextLine();
			// name 문자열 패턴 검색
			value = checkInputPattern(name, 2);
			if (!value) return;

			System.out.print("국어점수를 입력하세요 >>>>>> ");
			int kor = sc.nextInt();
			// kor (0~100) 패턴
			value = checkInputPattern(String.valueOf(kor), 3);
			if (!value) return;

			System.out.print("영어점수를 입력하세요 >>>>>> ");
			int eng = sc.nextInt();
			// eng (0~100) 패턴 검색
			value = checkInputPattern(String.valueOf(eng), 3);
			if (!value) return;

			System.out.print("수학점수를 입력하세요 >>>>>> ");
			int math = sc.nextInt();
			// math (0~100) 패턴 검색
			value = checkInputPattern(String.valueOf(math), 3);
			if (!value) return;

			// 데이타베이스 입력 객체생성
			Student student = new Student(no, name, kor, eng, math);
//			student.calTotal();
//			student.calAvr();
//			student.calGrade();

			DBConnection dbConn = new DBConnection();
			// 데이타베이스 연결
			dbConn.connect();
			// 테이블 데이터 입력
			int insertReturnValue = dbConn.insert(student);
			if (insertReturnValue == -1) {
				System.out.println("삽입 [실패]입니다!");
			} else {
				System.out.println("삽입 [성공]입니다!" + "리턴값: 1");
			}
			dbConn.close();
		} catch (InputMismatchException e) {
			System.out.println("입력타입이 맞지 않습니다. 재입력 바랍니다." + e.getMessage());
			return;
		} catch (Exception e) {
			System.out.println("데이타베이스 입력에러" + e.getMessage());
		} 
	}

	// 메뉴 선택
	public static int displayMenu() {
		// 메뉴 입력값 1입력, 수정, 삭제,검색, 출력, 정렬, 통계, 종료
		int num = -1;
		try {
			System.out.println("1.입력, 2.수정, 3.삭제, 4.검색, 5.출력, 6.정렬, 7.통계, 8.종료 \n 입력바랍니다 >>>>>>>");
			num = sc.nextInt();
			// 정수패턴 검색
			String pattern = "^[1-8]*$"; // 숫자만
			boolean regex = Pattern.matches(pattern, String.valueOf(num));
		} catch (InputMismatchException e) {
			System.out.println("InputMismatch [숫자]로 다시 입력 바랍니다");
			num = -1;
		} finally {
			// 입력장치 비움
			sc.nextLine();
		}
		return num;
	}

	// 문자열패턴 검색
	private static boolean checkInputPattern(String data, int patternType) {
		final int TYPENO = 1, TYPENAME = 2, TYPESUBJECT = 3, TYPESORT = 4, TYPESTATS = 5;
		String pattern = null;
		boolean regex = false;
		String message = null;
		switch (patternType) {
		case TYPENO:
			pattern = "^0[1-3]0[1-9][0-6][0-9]$";
			message = "학생번호 재입력 요망";
			break;
		case TYPENAME :
			pattern = "^[가-힣]{2,4}$";
			message = "이름 재입력 요망";
			break;
		case TYPESUBJECT :
			pattern = "^[0-9]{1,3}$";
			message = "과목 재입력 요망";
			break;
		case TYPESORT :
			pattern = "^[1-3]$";
			message = "정렬 재입력 요망";
			break;
		case TYPESTATS :
			pattern = "^[1-2]$";
			message = "통계 재입력 요망";
			break;
		}
		regex = Pattern.matches(pattern, data);
		if (patternType == 3) {
			if (!regex || Integer.parseInt(data) < 0 || Integer.parseInt(data) > 100) {
				System.out.println(message);
				return false;
			}
		} else {
			if (!regex) {
				System.out.println(message);
				return false;
			}
		}
		return regex;
	}
}

drop database if exists StudentDB;
create database if not exists StudentDB;
use StudentDB;
-- table student 생성
drop table if exists student;
create table student(
no char(6) not null primary Key,
name varchar(10) not null,
kor tinyint not null,
eng tinyint not null,
math tinyint not null,
total smallint null,
avr double null,
grade varchar(2) null,
rate int null default 0
);

create table deletestudent(
no char(6) not null,
name varchar(10) not null,
kor tinyint not null,
eng tinyint not null,
math tinyint not null,
total smallint null,
avr double null,
grade varchar(2) null,
rate int null,
deletedate datetime
);

create table updatestudent(
no char(6) not null,
name varchar(10) not null,
kor tinyint not null,
eng tinyint not null,
math tinyint not null,
total smallint null,
avr double null,
grade varchar(2) null,
rate int null,
updatetime datetime
);
-- 컬럼구조 확인
describe student; 

-- 인덱스 설정: name // create index (new필드명) on 테이블(필드);
create index idx_Student_name on student(name);


-- 삽입
insert into student values('020456','홍길동',100,100,100,300,100,'A',0);
insert into student values('020446','주길동',100,100,100,300,100,'A',0);
insert into student values('020436','화길동',100,100,100,300,100,'A',0);
insert into student values('020426','타길동',100,100,100,300,100,'A',0);
insert into student values('010203','강현모',100,100,100,300,100,'A',1);
-- 1. 삽입
insert into student(no,name,kor,eng,math,total,avr,grade,rate) values('021212','저길동',100,100,100,300,100,'A',2);

-- 2. 삭제
delete from student;
delete from student where no = '02066';
delete from student where name = '%홍길동%';

-- 3. 수정
UPDATE student SET kor = 60, eng = 50, math = 60, total = 180, avr = 60, grade = "F" where name = '만둥이';

-- 전체를 읽어줄것
select * from student;
select * from deletestudent;
select * from updatestudent;
select * from student where name like '%길동%';

-- 4. 정렬하기: 학번, 이름, 총점
select * from student order by name asc;
select * from student order by name desc;
select * from student order by total asc;
select * from student order by no asc;

-- 5. 최대값, 최소값 구하기
select max(total) from student;
select max(total) as '총점' from student;
select * from student order by total desc limit 1;
select min(total) from student;

-- 6. total = 300 인 사람의 정보를 출력하시오. (서브쿼리문)
select * from student where total = (select max(total) from student);
select * from student where total = (select min(total) from student);

-- 프로시저 생성 (합계, 평균, 등급 게산하는 프로시저)
drop procedure if exists procedure_insert_student;
delimiter $$
create procedure procedure_insert_student(
	IN in_no char(6),
    IN in_name varchar(10),
    IN in_kor int,
    IN in_eng int,
    IN in_math int
 )
begin
	-- 총점, 평균, 등급 변수 선언
    DECLARE in_total int default 0;
    DECLARE in_avr double default 0.0;
    DECLARE in_grade varchar(2) default null;
    -- 총점계산
    SET in_total = in_kor + in_eng + in_math;
    -- 평균계산
    SET in_avr = in_total / 3.0;
    -- 등급계산
    SET in_grade = 
		CASE
			WHEN in_avr >= 90.0	THEN 'A'
            WHEN in_avr >= 80.0	THEN 'B'
            WHEN in_avr >= 70.0	THEN 'C'
            WHEN in_avr >= 60.0	THEN 'D'
            ELSE 'F'
            END;
        
    -- 삽입 insert into student() values();
    insert into  student(no, name, kor, eng, math) 
		values(in_no, in_name, in_kor, in_eng, in_math);
    
	-- 수정 update student set 총점, 평균, 등급 where id = 등록한 아이디;
    UPDATE student SET total = in_total, avr = in_avr, grade = in_grade
		where no = in_no;
    
end $$
delimiter ;
call procedure_insert_student ();

--  트리거 생성
delimiter !!
create trigger trg_deleteStudent
	after delete
    on student
    for each row
begin
	INSERT INTO deletestudent VALUES
(old.no, old.name, old.kor, old.eng, old.math, old.total,
old.avr,old.grade,old.rate,now());

end !!
delimiter ;

delimiter //
create trigger trg_updatestudent
	before update 
    on student
    for each row
begin
 	INSERT INTO updatestudent VALUES
	(old.no, old.name, old.kor, old.eng, old.math, old.total,
	old.avr,old.grade,old.rate,now());
end //
delimiter ;

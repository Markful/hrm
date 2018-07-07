package hrm.service.impl;

import hrm.dao.*;
import hrm.domain.*;
import hrm.service.HrmService;
import hrm.service.OtherServiceInterface;
import hrm.util.tag.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**   
 * @Description: 人事管理系统服务层接口实现类 
 * @version V1.0   
 */
@Transactional(propagation=Propagation.REQUIRED,isolation=Isolation.DEFAULT)
@Service("hrmService")
public class HrmServiceImpl implements HrmService, OtherServiceInterface {

	/**
	 * 自动注入持久层对象
	 * */
	@Autowired
	private UserDao userDao;

	@Autowired
	private DeptDao deptDao;

	@Autowired
	private JobDao jobDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private NoticeDao noticeDao;

	@Autowired
	private DocumentDao documentDao;


	/*****************用户服务接口实现*************************************/
	/**
	 * HrmServiceImpl接口login方法实现
	 *  @see { HrmService }
	 * */
	@Transactional(readOnly=true)
	@Override
	public User login(String loginname, String password) {
//		System.out.println("HrmServiceImpl login -- >>");
		HashMap<String, String> hashMap= new HashMap<>();
		hashMap.put("loginname",  loginname );
		hashMap.put("password",  password );

		return userDao.selectByLoginnameAndPassword(hashMap);
	}

	/**
	 * HrmServiceImpl接口findUser方法实现
	 * @see { HrmService }
	 * */
	@Transactional(readOnly=true)
	@Override
	public List<User> findUser(User user,PageModel pageModel) {
		//这一层只完成模糊查询，不分页，分页操作在controller层完成

		List<User> users = null;
		Map<String ,Object> map = new HashMap<>();

			//往map中塞查询需要的参数
			map.put("username",user.getUsername() == null ? null : '%'+user.getUsername()+'%');
			map.put("status",user.getStatus());

			//模糊查找
			List<User> pageUser = userDao.selectByPage(map);

		System.out.println("pageUser==========" +pageUser);
            return pageUser;
		}
	
	/**
	 * HrmServiceImpl接口findUserById方法实现
	 * @see { HrmService }
	 * */
	@Transactional(readOnly=true)
	@Override
	public User findUserById(Integer id) {
		return userDao.selectById(id);
	}
	
	/**
	 * HrmServiceImpl接口removeUserById方法实现
	 * @see { HrmService }
	 * */
	@Override
	public void removeUserById(Integer id) {
 		userDao.deleteById(id);
	}

	/**
	 * HrmServiceImpl接口addUser方法实现
	 * @see { HrmService }
	 * */
	@Override
	public void modifyUser(User user) {
		userDao.update(user);

	}
	
	/**
	 * HrmServiceImpl接口modifyUser方法实现
	 * @see { HrmService }
	 * */
	@Override
	public void addUser(User user) {
 		userDao.save(user);
	}

	@Override
	public User findUserByName(String username) {
		return userDao.selectByName(username);
	}


	/*****************员工服务接口实现*************************************/
	@Transactional(readOnly=true)
	@Override
	public List<Employee> findEmployee(Employee employee, PageModel pageModel) {
		//这一层只完成模糊查询，不分页，分页操作在controller层完成

		List<Dept> depts = null;
		Map<String ,Object> map = new HashMap<>();

		//往map中塞查询需要的参数
		map.put("job_id",employee.getJob().getId());
		map.put("dept_id",employee.getDept().getId());
		map.put("sex",employee.getSex());
		map.put("name",employee.getName() == null ? null : '%'+employee.getName()+'%');
		map.put("cardId",employee.getCardId() == null ? null : '%'+employee.getCardId() + '%');
		map.put("phone",employee.getPhone() == null ? null : '%'+employee.getPhone() + '%');

		//模糊查找
		List<Employee> pageEmployee = employeeDao.selectByPage(map);

		return pageEmployee;

	}

	@Override
	public void removeEmployeeById(Integer id) {
		employeeDao.deleteById(id);
	}

	@Override
	public Employee findEmployeeById(Integer id) {
		return employeeDao.selectById(id);
	}

	@Override
	public void addEmployee(Employee employee) {
		employeeDao.save(employee);
	}

	@Override
	public void modifyEmployee(Employee employee) {
		employeeDao.update(employee);
	}

	@Override
	public List<Employee> findAllEmployee() {
		return employeeDao.selectAllEmployee();
	}



	/*****************部门服务接口实现*************************************/
	@Override
	public List<Dept> findDept(Dept dept, PageModel pageModel) {
		//这一层只完成模糊查询，不分页，分页操作在controller层完成

		List<Dept> depts = null;
		Map<String ,Object> map = new HashMap<>();

		//往map中塞查询需要的参数
		map.put("name",dept.getName() == null ? null : '%'+dept.getName()+'%');

		//模糊查找
		List<Dept> pageDept = deptDao.selectByPage(map);

		return pageDept;
	}

	@Override
	public List<Dept> findAllDept() {
		return deptDao.selectAllDept();
	}

	@Override
	public void removeDeptById(Integer id) {
		 deptDao.deleteById(id);
	}

	@Override
	public void addDept(Dept dept) {
		deptDao.save(dept);
	}

	@Override
	public Dept findDeptById(Integer id) {
		return deptDao.selectById(id);
	}

	@Override
	public void modifyDept(Dept dept) {
		deptDao.update(dept);
	}



	/*****************职位接口实现*************************************/

	@Override
	public List<Job> findAllJob() {
		return jobDao.selectAllJob();
	}


	@Override
	public List<Job> findJob(Job job, PageModel pageModel) {
		//这一层只完成模糊查询，不分页，分页操作在controller层完成

		List<Job> jobs = null;
		Map<String ,Object> map = new HashMap<>();

		//往map中塞查询需要的参数
		map.put("name",job.getName() == null ? null : '%'+job.getName()+'%');

		//模糊查找
		List<Job> pageJob = jobDao.selectByPage(map);

		return pageJob;
	}

	@Override
	public void removeJobById(Integer id) {
		jobDao.deleteById(id);
	}

	@Override
	public void addJob(Job job) {
		jobDao.save(job);
	}

	@Override
	public Job findJobById(Integer id) {
		return jobDao.selectById(id);
	}

	@Override
	public void modifyJob(Job job) {
		jobDao.update(job);
	}



	/*****************公告接口实现*************************************/

	@Override
	public List<Notice> findNotice(Notice notice, PageModel pageModel) {
		//这一层只完成模糊查询，不分页，分页操作在controller层完成

		List<Notice> notices = null;
		Map<String ,Object> map = new HashMap<>();

		//往map中塞查询需要的参数
		map.put("title",notice.getTitle() == null ? null : '%'+notice.getTitle()+'%');
		map.put("content",notice.getContent() == null ? null : '%'+notice.getContent()+'%');

		//模糊查找
		List<Notice> pageNotice = noticeDao.selectByPage(map);

		System.out.println("pageNotice==========" +pageNotice);
		return pageNotice;
	}

	@Override
	public Notice findNoticeById(Integer id) {
		return noticeDao.selectById(id);
	}

	@Override
	public void removeNoticeById(Integer id) {
		noticeDao.deleteById(id);
	}

	@Override
	public void addNotice(Notice notice) {
		noticeDao.save(notice);
	}

	@Override
	public void modifyNotice(Notice notice) {
		noticeDao.update(notice);
	}


	/*****************文件接口实现*************************************/

	@Override
	public List<Document> findDocument(Document document, PageModel pageModel) {
		//这一层只完成模糊查询，不分页，分页操作在controller层完成

		List<Document> notices = null;
		Map<String ,Object> map = new HashMap<>();

		//往map中塞查询需要的参数
		map.put("title",document.getTitle() == null ? null : '%'+document.getTitle()+'%');
		map.put("uid",document.getUser().getId() == null ? null : document.getUser().getId());

		//模糊查找
		List<Document> pageDocument = documentDao.selectByPage(map);

		System.out.println("pageDocument==========" +pageDocument);
		return pageDocument;
	}

	@Override
	public void addDocument(Document document) {
		documentDao.save(document);
	}

	@Override
	public Document findDocumentById(Integer id) {
		return documentDao.selectById(id);
	}

	@Override
	public void removeDocumentById(Integer id) {
		documentDao.deleteById(id);
	}

	@Override
	public void modifyDocument(Document document) {
		documentDao.update(document);
	}




}

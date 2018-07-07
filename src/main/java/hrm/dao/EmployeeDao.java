package hrm.dao;



import hrm.domain.Employee;

import java.util.List;
import java.util.Map;

/**   
 * @Description: EmployeeMapper接口
 * @version V1.0   
 */
public interface EmployeeDao {

	// 根据参数查询员工总数
 	Integer count(Map<String, Object> params);
	
	// 根据参数动态查询员工
  
	List<Employee> selectByPage(Map<String, Object> params);
	
	// 动态插入员工
 	void save(Employee employee);

	// 根据id删除员工
 	void deleteById(Integer id);
	
	// 根据id查询员工
  
	Employee selectById(Integer id);
	
	// 动态修改员工
 	void update(Employee employee);

    List<Employee> selectAllEmployee();


}

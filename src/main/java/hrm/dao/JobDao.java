package hrm.dao;



import hrm.domain.Job;

import java.util.List;
import java.util.Map;


/**   
 * @Description:  
 * @version V1.0   
 */
public interface JobDao {

 	Job selectById(int id);
	
 	List<Job> selectAllJob();

	// 动态查询
 	List<Job> selectByPage(Map<String, Object> params);
	
 	Integer count(Map<String, Object> params);
	
	// 根据id删除部门
 	void deleteById(Integer id);
	
	// 动态插入部门
 	void save(Job job);
	
	// 动态修改用户
 	void update(Job job);
}

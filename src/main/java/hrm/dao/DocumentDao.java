package hrm.dao;



import hrm.domain.Document;

import java.util.List;
import java.util.Map;

/**   
 * @Description: DocumentMapper接口  
 * @version V1.0   
 */
public interface DocumentDao {

	 
	List<Document> selectByPage(Map<String, Object> params);
		
 	Integer count(Map<String, Object> params);
		
	// 动态插入文档
 	void save(Document document);
	
 	Document selectById(int id);
	
	// 根据id删除文档
 	void deleteById(Integer id);
		
	// 动态修改文档
 	void update(Document document);
		
}

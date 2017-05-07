package weixin.zoo.infrastructure.repository;

import weixin.zoo.infrastructure.model.Form;

import java.util.List;

/**
 * Created by viczhang.zhangz on 2017/5/4.
 */
public interface FormRepository {

    /*
     * 保存表单
     */
    public long saveForm(String templateId, String formValue, String wxid, String name);

    /*
     * 根据表单id获取表单数据
     */
    public Form getFormById(long id);

    /*
     * 获取用户发起的表单
     */
    public List<Form> getFormByUserId(String userId);
}
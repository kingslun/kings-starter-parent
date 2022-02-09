package io.kings.framework.devops.openapi;


import io.kings.framework.devops.openapi.exception.ApplicationNotFoundException;
import io.kings.framework.devops.openapi.exception.ApplicationPatchException;
import io.kings.framework.devops.openapi.model.ApplicationVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 应用管理restapi
 *
 * @author lun.wang
 * @date 2021/7/30 7:16 下午
 * @since v1.0
 */
@RequestMapping("/v1/application")
@Tag(name = "应用管理", description = "应用相关restAPI")
public interface ApplicationOpenAPI {

    /**
     * 创建应用
     *
     * @param applicationVo 应用信息
     * @throws ApplicationPatchException 创建应用失败的描述对象
     */
    @Operation(description = "创建应用", method = "POST")
    @PostMapping("/create")
    void create(ApplicationVo applicationVo) throws ApplicationPatchException;

    /**
     * 删除应用
     *
     * @param id 应用ID
     * @throws ApplicationNotFoundException 删除不存在的应用应相应此错误
     */
    @Operation(description = "删除应用", method = "DELETE")
    @DeleteMapping("/delete/{id}")
    void delete(@PathVariable String id) throws ApplicationNotFoundException;

    /**
     * 编辑应用
     *
     * @param applicationVo 应用信息
     * @throws ApplicationPatchException 编辑应用失败的描述对象
     */
    @Operation(description = "编辑应用", method = "PUT")
    @PutMapping("/edit")
    void edit(ApplicationVo applicationVo) throws ApplicationPatchException;

    /**
     * 获取应用
     *
     * @param id 应用ID
     * @throws ApplicationNotFoundException 获取不存在应用应相应此错误
     */
    @Operation(description = "根据应用编号获取应用信息", method = "GET")
    @GetMapping("/get/{id}")
    ApplicationVo get(@PathVariable String id) throws ApplicationNotFoundException;
}

package cn.springcloud.gray.server.resources.rest;

import cn.springcloud.gray.api.ApiRes;
import cn.springcloud.gray.server.module.HandleModule;
import cn.springcloud.gray.server.module.domain.Handle;
import cn.springcloud.gray.server.module.domain.query.HandleQuery;
import cn.springcloud.gray.server.module.user.AuthorityModule;
import cn.springcloud.gray.server.resources.domain.fo.HandleFO;
import cn.springcloud.gray.server.utils.ApiResHelper;
import cn.springcloud.gray.server.utils.PaginationUtils;
import cn.springcloud.gray.server.utils.SessionUtils;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static cn.springcloud.gray.api.ApiRes.CODE_SUCCESS;

/**
 * @author saleson
 * @date 2020-05-31 00:36
 */
@RestController
@RequestMapping("/handle")
public class HandleResource {
    @Autowired
    private HandleModule handleModule;
    @Autowired
    private AuthorityModule authorityModule;

    @GetMapping(value = "/listAll")
    public ApiRes<List<Handle>> page(@Validated HandleQuery query) {
        List<Handle> records = handleModule.queryHandles(query);
        return ApiRes.<List<Handle>>builder()
                .code(CODE_SUCCESS)
                .data(records)
                .build();
    }

    @GetMapping(value = "/page")
    public ResponseEntity<ApiRes<List<Handle>>> page(
            @Validated HandleQuery query,
            @ApiParam @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Handle> page = handleModule.queryHandles(query, pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(page);
        return new ResponseEntity<>(
                ApiRes.<List<Handle>>builder()
                        .code(CODE_SUCCESS)
                        .data(page.getContent())
                        .build(),
                headers,
                HttpStatus.OK);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ApiRes<Void> delete(@PathVariable("id") Long id) {
        Handle handle = handleModule.getHandle(id);
        if (Objects.isNull(handle)) {
            return ApiResHelper.notFound();
        }
        if (!authorityModule.hasNamespaceAuthority(handle.getNamespace())) {
            return ApiResHelper.notAuthority();
        }
        handleModule.deleteHandle(id, SessionUtils.currentUserId());
        return ApiRes.<Void>builder().code(CODE_SUCCESS).build();
    }

    @RequestMapping(value = "/{id}/recover", method = RequestMethod.PATCH)
    public ApiRes<Void> recover(@PathVariable("id") Long id) {
        Handle handle = handleModule.getHandle(id);
        if (Objects.isNull(handle)) {
            return ApiResHelper.notFound();
        }
        if (!authorityModule.hasNamespaceAuthority(handle.getNamespace())) {
            return ApiResHelper.notAuthority();
        }
        handleModule.recoverHandle(id, SessionUtils.currentUserId());
        return ApiRes.<Void>builder().code(CODE_SUCCESS).build();
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ApiRes<Handle> save(@RequestBody HandleFO handleFO) {
        Handle handle = handleModule.getHandle(handleFO.getId());
        if (Objects.isNull(handle)) {
            handle = new Handle();
            handle.setId(handleFO.getId());
            handle.setNamespace(handleFO.getNamespace());
            handle.setType(handleFO.getType());
            handle.setDelFlag(false);
        }
        if (!authorityModule.hasNamespaceAuthority(handle.getNamespace())) {
            return ApiResHelper.notAuthority();
        }
        handle.setName(handleFO.getName());
        handle.setOperator(SessionUtils.currentUserId());
        handle.setOperateTime(new Date());

        return ApiRes.<Handle>builder()
                .code(CODE_SUCCESS)
                .data(handleModule.saveHandle(handle))
                .build();
    }

}

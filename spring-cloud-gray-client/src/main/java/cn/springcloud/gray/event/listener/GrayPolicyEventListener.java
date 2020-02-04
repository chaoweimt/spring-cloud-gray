package cn.springcloud.gray.event.listener;

import cn.springcloud.gray.UpdateableGrayManager;
import cn.springcloud.gray.local.InstanceLocalInfo;
import cn.springcloud.gray.local.InstanceLocalInfoInitiralizer;
import cn.springlcoud.gray.event.GrayPolicyEvent;
import org.apache.commons.lang3.StringUtils;

/**
 * @author saleson
 * @date 2020-02-03 18:18
 */
public class GrayPolicyEventListener extends AbstractGrayEventListener<GrayPolicyEvent> {


    private UpdateableGrayManager grayManager;

    private InstanceLocalInfoInitiralizer instanceLocalInfoInitiralizer;


    public GrayPolicyEventListener(
            UpdateableGrayManager grayManager, InstanceLocalInfoInitiralizer instanceLocalInfoInitiralizer) {
        this.grayManager = grayManager;
        this.instanceLocalInfoInitiralizer = instanceLocalInfoInitiralizer;
    }

    @Override
    protected void onUpdate(GrayPolicyEvent event) {
        grayManager.updatePolicyDefinition(
                event.getServiceId(), event.getInstanceId(), event.getSource());
    }

    @Override
    protected void onDelete(GrayPolicyEvent event) {

    }

    @Override
    protected boolean validate(GrayPolicyEvent event) {
        InstanceLocalInfo instanceLocalInfo = instanceLocalInfoInitiralizer.getInstanceLocalInfo();
        if (instanceLocalInfo != null) {
            if (StringUtils.equals(event.getServiceId(), instanceLocalInfo.getServiceId())) {
                return false;
            }
        }
        return true;
    }
}
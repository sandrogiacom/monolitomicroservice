package com.monolitomicroservice.teste.wildfly.extension;

import java.util.List;

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.wildfly.extension.undertow.ServletContainerService;
import org.wildfly.extension.undertow.UndertowService;

class CustomSubsystemAdd extends AbstractBoottimeAddStepHandler {
    static final CustomSubsystemAdd INSTANCE = new CustomSubsystemAdd();

    private CustomSubsystemAdd() {
    }

    @Override
    protected void populateModel(ModelNode operation, ModelNode model) throws OperationFailedException {
    }

    @Override
    public void performBoottime(OperationContext context, ModelNode operation, ModelNode model,
            ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers)
            throws OperationFailedException {

        CustomService service = new CustomService();
        ServiceName name = CustomService.createServiceName();

        final ServiceBuilder<CustomService> builder = context.getServiceTarget().addService(name, service);

        builder.addDependency(UndertowService.SERVLET_CONTAINER.append("default"), ServletContainerService.class, service.servletContainer);

        builder.setInitialMode(ServiceController.Mode.ACTIVE).install();
    }
}

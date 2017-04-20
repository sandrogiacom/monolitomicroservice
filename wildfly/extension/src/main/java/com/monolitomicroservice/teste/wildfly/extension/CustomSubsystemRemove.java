package com.monolitomicroservice.teste.wildfly.extension;

import org.jboss.as.controller.AbstractRemoveStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;

class CustomSubsystemRemove extends AbstractRemoveStepHandler {
    static final CustomSubsystemRemove INSTANCE = new CustomSubsystemRemove();

    private CustomSubsystemRemove() {
    }

    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model) throws OperationFailedException {
    }
}

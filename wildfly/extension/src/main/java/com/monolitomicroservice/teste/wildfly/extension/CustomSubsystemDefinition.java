package com.monolitomicroservice.teste.wildfly.extension;

import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.registry.ManagementResourceRegistration;

public class CustomSubsystemDefinition extends SimpleResourceDefinition {
    public static final CustomSubsystemDefinition INSTANCE = new CustomSubsystemDefinition();

    public CustomSubsystemDefinition() {
        super(CustomExtension.SUBSYSTEM_PATH,
                CustomExtension.getResourceDescriptionResolver(null),
                //We always need to add an 'add' operation
                CustomSubsystemAdd.INSTANCE,
                //Every resource that is added, normally needs a remove operation
                CustomSubsystemRemove.INSTANCE);
    }

    @Override
    public void registerOperations(ManagementResourceRegistration resourceRegistration) {
        super.registerOperations(resourceRegistration);
    }

    @Override
    public void registerAttributes(ManagementResourceRegistration resourceRegistration) {
    }
}

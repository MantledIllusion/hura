package com.mantledillusion.injection.hura.misc;

import com.mantledillusion.injection.hura.BeanAllocation;
import com.mantledillusion.injection.hura.Blueprint.BlueprintTemplate;
import com.mantledillusion.injection.hura.annotation.Define;
import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.injectables.Injectable;

public class InjectableInterfaceExtension implements BlueprintTemplate {

	public static final String RELAYED_INJECTABLE_SINGLETON_ID = "relayedInjectable";
	
	@Inject(RELAYED_INJECTABLE_SINGLETON_ID)
	private Injectable relayedInjectable;
	
	@Define
	private BeanAllocation<InjectableInterface> allocate() {
		return BeanAllocation.allocateToInstance(this.relayedInjectable);
	}
}

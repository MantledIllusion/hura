package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.annotation.Processed;
import com.mantledillusion.injection.hura.annotation.Processed.PhasedProcessor;
import com.mantledillusion.injection.hura.misc.PostProcessorAtConstructPhase;
import com.mantledillusion.injection.hura.misc.PostProcessorAtDestroyPhase;
import com.mantledillusion.injection.hura.misc.PostProcessorAtFinalizePhase;
import com.mantledillusion.injection.hura.misc.PostProcessorAtInjectPhase;
import com.mantledillusion.injection.hura.misc.PostProcessorAtInspectPhase;

@Processed({@PhasedProcessor(value=PostProcessorAtInspectPhase.class, phase=Phase.INSPECT),
	@PhasedProcessor(value=PostProcessorAtConstructPhase.class, phase=Phase.CONSTRUCT),
	@PhasedProcessor(value=PostProcessorAtInjectPhase.class, phase=Phase.INJECT),
	@PhasedProcessor(value=PostProcessorAtFinalizePhase.class, phase=Phase.FINALIZE),
	@PhasedProcessor(value=PostProcessorAtDestroyPhase.class, phase=Phase.DESTROY)})
public class InjectableWithProcessor extends InjectableWithProcessableFields {

}

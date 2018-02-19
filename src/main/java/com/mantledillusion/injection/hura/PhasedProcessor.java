package com.mantledillusion.injection.hura;

import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.annotation.Processed;

/**
 * Instantiable version of {@link Processed.PhasedProcessor}.
 *
 * @param <T>
 *            The bean type this applicator might be applied on.
 */
public final class PhasedProcessor<T> {

	private final Processor<T> processor;
	private final Phase phase;

	public Processor<T> getPostProcessor() {
		return processor;
	}

	public Phase getPhase() {
		return phase;
	}

	private PhasedProcessor(Processor<T> processor, Phase phase) {
		this.processor = processor;
		this.phase = phase;
	}

	/**
	 * Creates a new {@link PhasedProcessor}.
	 * 
	 * @param <T>
	 *            The bean type that can be processed.
	 * @param processor
	 *            The {@link Processor} to apply on an instantiated bean; may
	 *            <b>not</b> be null.
	 * @param phase
	 *            The {@link Phase} during which to apply the given
	 *            {@link Processor}; may <b>not</b> be null.
	 * @return A new {@link PhasedProcessor}; never null
	 */
	public static <T> PhasedProcessor<T> of(Processor<T> processor, Phase phase) {
		if (processor == null) {
			throw new IllegalArgumentException("The processor to apply cannot be null.");
		} else if (phase == null) {
			throw new IllegalArgumentException("The phase during which to apply a processor cannot be null.");
		}
		return new PhasedProcessor<>(processor, phase);
	}
}

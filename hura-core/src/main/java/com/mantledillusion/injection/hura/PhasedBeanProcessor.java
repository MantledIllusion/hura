package com.mantledillusion.injection.hura;

import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.annotation.lifecycle.bean.BeanProcessor;

/**
 * Tuple of a {@link Phase} and a {@link BeanProcessor}.
 *
 * @param <T>
 *            The bean type the {@link BeanProcessor} might be applied on.
 */
public final class PhasedBeanProcessor<T> {

	private final BeanProcessor<T> processor;
	private final Phase phase;

	public BeanProcessor<T> getPostProcessor() {
		return processor;
	}

	public Phase getPhase() {
		return phase;
	}

	private PhasedBeanProcessor(BeanProcessor<T> processor, Phase phase) {
		this.processor = processor;
		this.phase = phase;
	}

	/**
	 * Creates a new {@link PhasedBeanProcessor}.
	 * 
	 * @param <T>
	 *            The bean type that can be processed.
	 * @param processor
	 *            The {@link BeanProcessor} to apply on an instantiated bean; may
	 *            <b>not</b> be null.
	 * @param phase
	 *            The {@link Phase} during which to apply the given
	 *            {@link BeanProcessor}; may <b>not</b> be null.
	 * @return A new {@link PhasedBeanProcessor}; never null
	 */
	public static <T> PhasedBeanProcessor<T> of(BeanProcessor<T> processor, Phase phase) {
		if (processor == null) {
			throw new IllegalArgumentException("The processor to apply cannot be null.");
		} else if (phase == null) {
			throw new IllegalArgumentException("The phase during which to apply a processor cannot be null.");
		}
		return new PhasedBeanProcessor<>(processor, phase);
	}
}

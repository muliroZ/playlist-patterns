package com.playlist.decorator;

/**
 * Efeito que aplica uma rampa linear de volume nas primeiras amostras.
 */
public final class FadeInEffect extends AudioEffect {

  private final int sampleCount;

  /**
   * Cria o efeito de fade in.
   *
   * @param wrapped     áudio decorado.
   * @param sampleCount quantidade de amostras usadas na rampa.
   */
  public FadeInEffect(AudioTrack wrapped, int sampleCount) {
    super(wrapped);
    this.sampleCount = sampleCount;
  }

  @Override
  protected String describe() {
    return "fadeIn(%d)".formatted(sampleCount);
  }

  @Override
  public double[] getSamples() {
    if (sampleCount == 0) {
      return wrapped.getSamples();
    }

    double[] samples = wrapped.getSamples();
    for (int i = 0; i < sampleCount; i++) {
      samples[i] *= ((double) i / sampleCount);
    }

    return samples;
  }
}

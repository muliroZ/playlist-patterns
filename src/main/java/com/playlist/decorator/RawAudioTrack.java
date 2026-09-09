package com.playlist.decorator;

/**
 * Componente concreto do padrão Decorator: o áudio original, sem nenhum efeito.

 * Este arquivo não deve ser alterado.
 */
public final class RawAudioTrack implements AudioTrack {

  private final String title;
  private final double[] samples;

  /**
   * Cria o áudio original.
   *
   * @param title título da faixa.
   * @param samples amostras de áudio. O array é copiado.
   */
  public RawAudioTrack(String title, double[] samples) {
    if (title == null || samples == null) {
      throw new IllegalArgumentException("title e samples não podem ser nulos");
    }
    this.title = title;
    this.samples = samples.clone();
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public double[] getSamples() {
    return samples.clone();
  }

  @Override
  public String getEffectChain() {
    return "original";
  }
}

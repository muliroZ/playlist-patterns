package com.playlist.decorator;

import java.util.Arrays;
import java.util.Locale;

/**
 * Efeito que multiplica o volume das amostras, com corte em
 * {@code [-1.0, 1.0]}.
 */
public final class VolumeEffect extends AudioEffect {

  private final double factor;

  /**
   * Cria o efeito de volume.
   *
   * @param wrapped áudio decorado.
   * @param factor  fator multiplicador do volume.
   */
  public VolumeEffect(AudioTrack wrapped, double factor) {
    super(wrapped);
    this.factor = factor;
  }

  @Override
  protected String describe() {
    return String.format(Locale.ROOT, "volume(%.1f)", factor);
  }

  @Override
  public double[] getSamples() {
    return Arrays.stream(wrapped.getSamples())
        .map(s -> s * factor > 1.0 ? 1.0 : s * factor)
        .map(s -> s < -1.0 ? -1.0 : s)
        .toArray();
  }
}

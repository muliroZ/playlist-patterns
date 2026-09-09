package com.playlist.facade;

import com.playlist.adapter.TrackCatalog;
import com.playlist.composite.PlaylistNode;
import com.playlist.composite.TrackItem;
import com.playlist.core.Subscription;
import com.playlist.core.Track;
import com.playlist.core.TrackNotFoundException;
import com.playlist.decorator.AudioTrack;
import com.playlist.decorator.FadeInEffect;
import com.playlist.decorator.RawAudioTrack;
import com.playlist.decorator.VolumeEffect;
import com.playlist.proxy.AudioStream;
import com.playlist.proxy.ProtectedAudioStreamProxy;

/**
 * Fachada que esconde do mundo externo a colaboração entre catálogo, playlists,
 * streams protegidos e efeitos de áudio.

 * Quem usa a Playlist precisa conhecer apenas esta classe.
 */
public class PlaylistFacade {

  private final TrackCatalog catalog;
  private final Subscription plan;
  private AudioStream audioStream;

  /**
   * Monta a fachada.
   *
   * @param catalog catálogo de faixas já adaptado.
   * @param plan    plano de assinatura de quem está usando o sistema.
   * @throws IllegalArgumentException se qualquer argumento for nulo.
   */
  public PlaylistFacade(TrackCatalog catalog, Subscription plan) {
    if (catalog == null || plan == null) {
      throw new IllegalArgumentException();
    }

    this.catalog = catalog;
    this.plan = plan;
  }

  /**
   * Monta uma playlist com todas as faixas do catálogo, na ordem em que o
   * catálogo as devolve.
   *
   * @param name nome da playlist criada.
   * @return a playlist preenchida.
   */
  public PlaylistNode buildLibrary(String name) {
    PlaylistNode playlist = new PlaylistNode(name);
    catalog.findAll().forEach(t -> {
      playlist.add(new TrackItem(t));
    });

    return playlist;
  }

  /**
   * Devolve os bytes de áudio de uma faixa, respeitando o plano de assinatura.
   *
   * @param trackId identificador da faixa.
   * @return os bytes do áudio.
   * @throws TrackNotFoundException se a faixa não existir no catálogo.
   */
  public byte[] listen(String trackId) {
    Track track = catalog.findById(trackId)
        .orElseThrow(() -> new TrackNotFoundException("Faixa não encontrada no catálogo."));

    if (audioStream == null || audioStream.getTrackId() != trackId) {
      audioStream = new ProtectedAudioStreamProxy(track, plan);
    }

    return audioStream.readBytes();
  }

  /**
   * Monta uma prévia da faixa com volume ajustado e fade in.
   *
   * @param trackId       identificador da faixa.
   * @param volume        fator de volume aplicado primeiro.
   * @param fadeInSamples quantidade de amostras do fade in, aplicado depois.
   * @return o áudio já decorado.
   * @throws TrackNotFoundException se a faixa não existir no catálogo.
   */
  public AudioTrack preview(String trackId, double volume, int fadeInSamples) {
    Track track = catalog.findById(trackId)
        .orElseThrow(() -> new TrackNotFoundException("Faixa não encontrada no catálogo."));
    byte[] audio = listen(trackId);

    double[] samples = new double[audio.length];
    for (int i = 0; i < audio.length; i++) {
      samples[i] = audio[i] / 128.0;
    }
    return new FadeInEffect(
        (new VolumeEffect(
            (new RawAudioTrack(
                track.title(), samples)),
            volume)),
        fadeInSamples);
  }
}

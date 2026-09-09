package com.playlist.composite;

import com.playlist.core.Track;
import java.util.List;

/**
 * Componente do padrão Composite: qualquer item que possa aparecer dentro de uma playlist.
 * Tanto uma faixa isolada (TrackItem) quanto uma playlist inteira (PlaylistNode) são MediaItems,
 * e por isso podem ser tratadas de forma uniforme por quem consome a biblioteca.
 */
public interface MediaItem {
  String getName();

  int getDurationSeconds();

  int getTrackCount();
  
  List<Track> flatten();
}

# WordRunner 2048

## Overview
The classic game, Crossword, with a slightly altered gameplay using JavaFX. All the words used, are from the descriptions of books @[openlibrary.org](www.openlibrary.org). Graphics made in Photoshop.

## Hint System
For each letter of the hidden word, not yet guessed correctly by the player, a set 'hint' letters is selected and provided on the GUI. This set contains letters, which belong to words that:
1. Have the same length as that of the hidden word.
2. Share the same letters as the ones correctly guessed by the player at the same position.

## Modes
- Random: Picks a word from a random local set of words(dictionary).
- Load: Picks a word from a local set of words(dictionary).
- Download: Pick a theme from 4 predefined themes (Sci-Fi, Mystery, Horror, Fantasy) and play with a word corresponding to those themes.

## Animated grid
Triangle mesh animated for each frame by a function transforming the Z points using a random Gaussian.

## Releases 
- Linux Java16
- Linux Java11

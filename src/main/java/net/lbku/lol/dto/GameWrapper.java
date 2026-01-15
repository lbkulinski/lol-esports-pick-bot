package net.lbku.lol.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record GameWrapper(@JsonAlias("title") Game game) {
}

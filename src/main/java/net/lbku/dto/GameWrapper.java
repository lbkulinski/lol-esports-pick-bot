package net.lbku.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record GameWrapper(@JsonAlias("title") Game game) {
}

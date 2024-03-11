package com.server.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class VisitRequest {
    private Integer locationID;
    private String locationVisitor;

    public boolean isValid() {
        return (locationID != null && locationVisitor != null);
    }
}

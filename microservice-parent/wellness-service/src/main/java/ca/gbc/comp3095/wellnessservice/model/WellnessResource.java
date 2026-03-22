package ca.gbc.comp3095.wellnessservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class WellnessResource implements Serializable {


    @Id
    private String id;
    private String title;
    private String description;
    private String url;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

}

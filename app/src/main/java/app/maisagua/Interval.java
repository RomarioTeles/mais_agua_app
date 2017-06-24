package app.maisagua;

/**
 * Created by romario on 16/06/17.
 */

public class Interval {

    private Double time;

    private String descricao;

    public Interval(Double time, String descricao) {
        this.time = time;
        this.descricao = descricao;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

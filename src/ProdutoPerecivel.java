import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ProdutoPerecivel extends Produto {

	private static final double TAXA_DESCONTO = 0.25;
	private static final int DIAS_LIMITE_DESCONTO = 7;
	
	private final LocalDate validade;
	
	public ProdutoPerecivel(String desc, double custo, double margem, LocalDate validade) {
		super(desc, custo, margem);
		if (validade.isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("Validade anterior ao dia de hoje!");
		}
		this.validade = validade;
	}
	
	public ProdutoPerecivel(String desc, double custo, LocalDate validade) {
		super(desc, custo);
		if (validade.isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("Validade anterior ao dia de hoje!");
		}
		this.validade = validade;
	}

	@Override
	public double valorDeVenda() {
		double preco = custo * (1.0 + margem);
		if (proximoVencimento()) {
			preco *= (1.0 - TAXA_DESCONTO);
		}
		return preco;
	}

	private boolean proximoVencimento() {
		long dias = ChronoUnit.DAYS.between(LocalDate.now(), validade);
		return dias >= 0 && dias <= DIAS_LIMITE_DESCONTO;
	}

	@Override
	public String toString() {
		return String.format("%s\nVálido até: %s", super.toString(), FORMATO_DATA.format(validade));
	}

	@Override
	public String gerarDadosTexto() {
		return String.format("2;%s;%.2f;%.2f;%s", 
			descricao, custo, margem, FORMATO_DATA.format(validade));
	}
}
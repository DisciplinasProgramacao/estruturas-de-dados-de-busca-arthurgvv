import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public abstract class Produto implements Comparable<Produto> {
	
	protected static final double MARGEM_PADRAO = 0.2;
	protected static final int ID_INICIAL = 10_000;
	protected static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	protected static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance();
	protected static final int TAM_MINIMO_DESC = 3;
	protected static final double PRECO_MINIMO = 0.01;
	protected static final double MARGEM_MINIMA = 0.01;
	
	private static int proximoID = ID_INICIAL;
	
	protected int id;
	public String descricao;
	protected double custo;
	protected double margem;
	
	protected Produto(String desc, double preco, double margem) {
		validar(desc, preco, margem);
		this.id = proximoID++;
		this.descricao = desc;
		this.custo = preco;
		this.margem = margem;
	}
	
	protected Produto(String desc, double preco) {
		this(desc, preco, MARGEM_PADRAO);
	}
	
	private static void validar(String desc, double preco, double margem) {
		if (desc == null || desc.length() < TAM_MINIMO_DESC) {
			throw new IllegalArgumentException("Descrição inválida (mínimo " + TAM_MINIMO_DESC + " caracteres)");
		}
		if (preco < PRECO_MINIMO) {
			throw new IllegalArgumentException("Preço deve ser maior que " + PRECO_MINIMO);
		}
		if (margem < MARGEM_MINIMA) {
			throw new IllegalArgumentException("Margem deve ser maior que " + MARGEM_MINIMA);
		}
	}
	
	public abstract double valorDeVenda();
	
	@Override
	public String toString() {
		return String.format("ID: %d NOME: %s: %s", id, descricao, MOEDA.format(valorDeVenda()));
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Produto)) return false;
		return this.id == ((Produto) obj).id;
	}

	@Override
	public int compareTo(Produto outro) {
		return Integer.compare(this.id, outro.id);
	}

	static Produto criarDoTexto(String linha) {
		String[] dados = linha.split(";");
		int tipo = Integer.parseInt(dados[0]);
		String desc = dados[1];
		double custo = parseDouble(dados[2]);
		double margem = parseDouble(dados[3]);
		
		if (tipo == 2) {
			LocalDate validade = LocalDate.parse(dados[4], FORMATO_DATA);
			return new ProdutoPerecivel(desc, custo, margem, validade);
		} else {
			return new ProdutoNaoPerecivel(desc, custo, margem);
		}
	}

	private static double parseDouble(String valor) {
		return Double.parseDouble(valor.replace(",", "."));
	}

	public abstract String gerarDadosTexto();
}
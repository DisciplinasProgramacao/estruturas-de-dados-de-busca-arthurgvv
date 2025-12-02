import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

public class Pedido implements Comparable<Pedido> {

	private static int proximoID = 1;
	private static final double TAXA_DESCONTO_AVISTA = 0.15;
	private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	private final int idPedido;
	private final LocalDate dataCriacao;
	private final FormaPagamento forma;
	private final Lista<Produto> itens;
	private int totalItens;
	
	private enum FormaPagamento {
		AVISTA(1, "à vista", TAXA_DESCONTO_AVISTA),
		PARCELADO(2, "parcelado", 0.0);
		
		@SuppressWarnings("unused")
		final int codigo;
		final String descricao;
		final double desconto;
		
		FormaPagamento(int codigo, String descricao, double desconto) {
			this.codigo = codigo;
			this.descricao = descricao;
			this.desconto = desconto;
		}
		
		static FormaPagamento de(int codigo) {
			return codigo == 1 ? AVISTA : PARCELADO;
		}
	}
	
	public Pedido(LocalDate data, int tipoPagamento) {
		this.idPedido = proximoID++;
		this.dataCriacao = data;
		this.forma = FormaPagamento.de(tipoPagamento);
		this.itens = new Lista<>();
		this.totalItens = 0;
	}
	
	public boolean adicionarProduto(Produto produto) {
		if (produto == null) return false;
		itens.inserir(produto, totalItens++);
		return true;
	}
	
	public double calcularTotal() {
		double soma = itens.calcularValorTotal(Produto::valorDeVenda);
		return aplicarDesconto(soma);
	}
	
	private double aplicarDesconto(double valor) {
		valor *= (1.0 - forma.desconto);
		BigDecimal bd = new BigDecimal(Double.toString(valor));
		bd = bd.setScale(2, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Número do pedido: %02d%n", idPedido));
		sb.append("Data do pedido: ").append(FORMATADOR.format(dataCriacao)).append("\n");
		sb.append("Pedido com ").append(totalItens).append(" produtos.\n");
		sb.append("Produtos no pedido:\n");
		sb.append(itens.toString());
		sb.append("Pedido pago ").append(forma.descricao);
		if (forma.desconto > 0) {
			sb.append(". Percentual de desconto: ").append(String.format("%.2f", forma.desconto * 100)).append("%\n");
		} else {
			sb.append(".\n");
		}
		sb.append("Valor total do pedido: R$ ").append(String.format("%.2f", calcularTotal())).append("\n");
		return sb.toString();
	}

	@Override
	public int compareTo(Pedido outro) {
		return Integer.compare(this.idPedido, outro.idPedido);
	}
	
	public LocalDate obterData() {
		return dataCriacao;
	}
	
	public int obterID() {
		return idPedido;
	}
	
	public int obterQuantidade() {
		return totalItens;
	}
	
	public Lista<Produto> obterProdutos() {
		return itens;
	}
	
	public int contarRepeticoes(Produto produto) {
		Predicate<Produto> filtro = prod -> prod.descricao.equals(produto.descricao);
		return itens.contarRepeticoes(filtro);
	}
}
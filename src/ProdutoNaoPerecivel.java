public class ProdutoNaoPerecivel extends Produto {

	public ProdutoNaoPerecivel(String desc, double custo, double margem) {
		super(desc, custo, margem);
	}
	
	public ProdutoNaoPerecivel(String desc, double custo) {
		super(desc, custo);
	}

	@Override
	public double valorDeVenda() {
		return custo * (1.0 + margem);
	}

	@Override
	public String gerarDadosTexto() {
		return String.format("1;%s;%.2f;%.2f", descricao, custo, margem);
	}
}
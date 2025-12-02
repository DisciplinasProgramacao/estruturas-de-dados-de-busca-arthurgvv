public class No<K, V> {

	private K chave;
	private V valor;
	private No<K, V> dir;
	private No<K, V> esq;
	private int h;
	
	public No(K chave, V valor) {
		this.chave = chave;
		this.valor = valor;
		this.dir = this.esq = null;
		this.h = 0;
	}

	public V getItem() {
		return valor;
	}

	public void setItem(V valor) {
		this.valor = valor;
	}

	public K getChave() {
		return chave;
	}

	public void setChave(K chave) {
		this.chave = chave;
	}
	
	public No<K, V> getDireita() {
		return dir;
	}

	public void setDireita(No<K, V> direita) {
		this.dir = direita;
	}

	public No<K, V> getEsquerda() {
		return esq;
	}

	public void setEsquerda(No<K, V> esquerda) {
		this.esq = esquerda;
	}
	
	private int obterAltura(No<K, V> node) {
		return (node != null) ? node.h : -1;
	}
	
	public int getAltura() {
		return h;
	}

	public void setAltura() {
		h = Math.max(obterAltura(esq), obterAltura(dir)) + 1;
	}
	
	public int getFatorBalanceamento() {
		return obterAltura(esq) - obterAltura(dir);
	}	
}
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

public class Lista<E> implements IMedicao {

	private final Celula<E> sentinela;
	private Celula<E> final_;
	private int tam;
	private long comp;
	private long t0;
	private long t1;
	
	public Lista() {
		this.sentinela = this.final_ = new Celula<>();
		this.tam = 0;
	}
	
	public boolean vazia() {
		return this.sentinela == this.final_;
	}
	
	public void inserir(E elemento, int idx) {
		if (idx < 0 || idx > tam)
			throw new IndexOutOfBoundsException("Posição inválida!");
		
		Celula<E> ant = sentinela;
		for (int i = 0; i < idx; i++)
			ant = ant.getProximo();
			
		Celula<E> nova = new Celula<>(elemento);
		nova.setProximo(ant.getProximo());
		ant.setProximo(nova);
		
		if (idx == tam) this.final_ = nova;
		this.tam++;		
	}
	
	public void inserirFinal(E elemento) {
		Celula<E> nova = new Celula<>(elemento);
		this.final_.setProximo(nova);
		this.final_ = nova;
		this.tam++;
	}
	
	private E removerProxima(Celula<E> ant) {
		Celula<E> rem = ant.getProximo();
		Celula<E> prox = rem.getProximo();
		
		ant.setProximo(prox);
		rem.setProximo(null);
		
		if (rem == this.final_) this.final_ = ant;
		this.tam--;
		
		return rem.getItem();	
	}
	
	public E remover(int idx) {
		if (vazia())
			throw new IllegalStateException("Lista vazia!");
		
		if (idx < 0 || idx >= tam)
			throw new IndexOutOfBoundsException("Posição inválida!");
			
		Celula<E> ant = sentinela;
		for (int i = 0; i < idx; i++)
			ant = ant.getProximo();
				
		return removerProxima(ant);
	}
	
	public E remover(E elemento) {
		if (vazia())
			throw new IllegalStateException("Lista vazia!");
		
		Celula<E> ant = sentinela;
		while (ant.getProximo() != null && !ant.getProximo().getItem().equals(elemento))
			ant = ant.getProximo();
		
		if (ant.getProximo() == null)
			throw new NoSuchElementException("Elemento não encontrado!");
		
		return removerProxima(ant);
	}
	
	public E pesquisar(E procurado) {
		comp = 0;
		t0 = System.nanoTime();
		
		Celula<E> aux = sentinela.getProximo();
		
		while (aux != null) {
			comp++;
			if (aux.getItem().equals(procurado)) {
				t1 = System.nanoTime();
				return aux.getItem();
			}
			aux = aux.getProximo();
		}
		
		throw new NoSuchElementException("Elemento não encontrado!");
	}
	
	@Override
	public String toString() {
		if (vazia()) return "Lista vazia!\n";
		
		StringBuilder sb = new StringBuilder();
		Celula<E> aux = sentinela.getProximo();
		while (aux != null) {
			sb.append(aux.getItem()).append("\n");
			aux = aux.getProximo();
		}
		return sb.toString();
	}
	
    public int contarRepeticoes(Predicate<E> filtro) {
        int cont = 0;
        Celula<E> aux = sentinela.getProximo();
    	while (aux != null) {
    		if (filtro.test(aux.getItem())) cont++;
    		aux = aux.getProximo();
    	}
    	return cont;
	}
    
   	public double calcularValorTotal(Function<E, Double> extrator) {
   		if (vazia()) throw new IllegalStateException("Lista vazia!");
   		
   		double total = 0;
   		Celula<E> aux = sentinela.getProximo();
   		while (aux != null) {
   			total += extrator.apply(aux.getItem());
   			aux = aux.getProximo();
   		}
   		return total;
   	}
   	
	public int tamanho() {
		return tam;
	}

	@Override
	public long getComparacoes() {
		return comp;
	}

	@Override
	public double getTempo() {
		return (t1 - t0) / 1_000_000.0;
	}
}

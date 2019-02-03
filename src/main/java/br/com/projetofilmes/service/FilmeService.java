package br.com.projetofilmes.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.projetofilmes.service.ServiceException;
import br.com.projetofilmes.domain.Avaliacao;
import br.com.projetofilmes.domain.Filme;
import br.com.projetofilmes.domain.Genero;
import br.com.projetofilmes.domain.Usuario;
import br.com.projetofilmes.dto.AvaliacaoDTO;
import br.com.projetofilmes.dto.FilmeDTO;
import br.com.projetofilmes.repository.FilmeRepository;
import br.com.projetofilmes.repository.GeneroRepository;
import br.com.projetofilmes.repository.UsuarioRepository;

@Service
@Transactional
public class FilmeService {

	private FilmeRepository filmeRepository;

	private GeneroRepository generoRepository;

	private UsuarioRepository usuarioRepository;

	@Autowired
	public FilmeService(FilmeRepository filmeRepository, GeneroRepository generoRepository) {
		this.filmeRepository = filmeRepository;
		this.generoRepository = generoRepository;
	}

	public void deleteAll() {
		this.filmeRepository.deleteAll();
	}

	public void save(FilmeDTO filmeDTO) {
		Optional<Genero> encontrarGenero = generoRepository.findByName(filmeDTO.getGenero());
		String titulo = filmeDTO.getTitulo();
		LocalDate dataLancamento = filmeDTO.getDataLancamento();
		String nomeDiretor = filmeDTO.getNomeDiretor();
		Genero genero = encontrarGenero.get();

		Filme filme = new Filme(titulo, dataLancamento, nomeDiretor, genero);
		this.filmeRepository.saveAndFlush(filme);

		filmeDTO.setId(filme.getId());
	}

	public FilmeDTO findById(Integer id) {
		Optional<Filme> filme = filmeRepository.findById(id);
		if (filme.isPresent()) {
			FilmeDTO filmeDTO = criarFilmeDTO(filme.get());
			return filmeDTO;
		}
		throw new ServiceException("Filme não encontrado");
	}

	private FilmeDTO criarFilmeDTO(Filme filme) {
		FilmeDTO filmeDTO = new FilmeDTO();
		filmeDTO.setId(filme.getId());
		filmeDTO.setTitulo(filme.getTitulo());
		filmeDTO.setDataLancamento(filme.getDataLancamento());
		filmeDTO.setNomeDiretor(filme.getNomeDiretor());
		filmeDTO.setGenero(filme.getGenero().getNome());
		filmeDTO.setAvaliacao(criarAvaliacao(filme.getAvaliacao()));
		return filmeDTO;
	}

	private List<AvaliacaoDTO> criarAvaliacao(List<Avaliacao> avaliacoes) {
		List<AvaliacaoDTO> resposta = new ArrayList<>();

		for (Avaliacao avaliacao : avaliacoes) {
			AvaliacaoDTO avaliacaoDTO = new AvaliacaoDTO();
			avaliacaoDTO.setId(avaliacao.getId());
			avaliacaoDTO.setIdFilme(avaliacao.getFilme().getId());
			avaliacaoDTO.setUsuario(avaliacao.getUsuario().getEmail());
			avaliacaoDTO.setNota(avaliacao.getNota());
			resposta.add(avaliacaoDTO);
		}
		return resposta;
	}

	public List<FilmeDTO> findAll() {
		List<FilmeDTO> todosOsFilmes = new ArrayList<FilmeDTO>();
		List<Filme> filmes = filmeRepository.findAll();

		for (Filme filme : filmes) {
			FilmeDTO filmeDTO = criarFilmeDTO(filme);
			todosOsFilmes.add(filmeDTO);
		}
		return todosOsFilmes;
	}

	public void delete(Integer id) {
		this.filmeRepository.deleteById(id);
	}

	public void update(FilmeDTO filmeDTO) {
		Optional<Genero> encontrarGenero = generoRepository.findByName(filmeDTO.getGenero());
		String titulo = filmeDTO.getTitulo();
		LocalDate dataLancamento = filmeDTO.getDataLancamento();
		String nomeDiretor = filmeDTO.getNomeDiretor();
		Genero genero = encontrarGenero.get();

		Filme filme = new Filme(titulo, dataLancamento, nomeDiretor, genero);
		this.filmeRepository.saveAndFlush(filme);
		filmeDTO.setId(filme.getId());
	}

	public void adicionarAvaliacao(AvaliacaoDTO avaliacaoDTO) {
		Optional<Filme> filmeEncontrado = this.filmeRepository.findById(avaliacaoDTO.getIdFilme());
		Optional<Usuario> usuarioEncontrado = this.usuarioRepository.findByEmail(avaliacaoDTO.getUsuario());
		if (filmeEncontrado.isPresent()) {
			Filme filme = filmeEncontrado.get();
			Usuario usuario = usuarioEncontrado.get();
			Integer nota = avaliacaoDTO.getNota();
			Avaliacao avaliacao = new Avaliacao(usuario, filme, nota);
			filme.adicionarAvaliacao(avaliacao);
			this.filmeRepository.saveAndFlush(filme);
		}
	}

	public FilmeDTO findByTitulo(String titulo) {
		Optional<Filme> filme = filmeRepository.findByTitulo(titulo);
		if (filme.isPresent()) {
			FilmeDTO filmeDTO = criarFilmeDTO(filme.get());
			return filmeDTO;
		}
		throw new ServiceException("Filme não encontrado!");
	}

}

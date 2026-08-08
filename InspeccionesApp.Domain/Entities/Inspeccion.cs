namespace InspeccionesApp.Domain.Entities;

public class Inspeccion
{
    public int Id { get; set; }
    public int UsuarioId { get; set; }
    public string Titulo { get; set; } = string.Empty;
    public string Descripcion { get; set; } = string.Empty;
    public DateTime FechaInspeccion { get; set; }
    public decimal Latitud { get; set; }
    public decimal Longitud { get; set; }
    public string Estado { get; set; } = "Abierta";

    public Usuario? Usuario { get; set; }
    public ICollection<Fotografia> Fotografias { get; set; } = new List<Fotografia>();
    public ICollection<Audio> Audios { get; set; } = new List<Audio>();
    public ICollection<Observacion> Observaciones { get; set; } = new List<Observacion>();
    public ICollection<Historial> Historial { get; set; } = new List<Historial>();
}
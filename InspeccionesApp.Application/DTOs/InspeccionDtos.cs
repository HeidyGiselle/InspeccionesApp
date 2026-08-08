namespace InspeccionesApp.Application.DTOs
{
    public class InspeccionCreateDto
    {
        public string Titulo { get; set; } = string.Empty;
        public string Descripcion { get; set; } = string.Empty;
        public DateTime FechaInspeccion { get; set; }
      public decimal Latitud { get; set; }
     public decimal Longitud { get; set; }
    }

    public class InspeccionUpdateDto
    {
        public string Titulo { get; set; } = string.Empty;
        public string Descripcion { get; set; } = string.Empty;
        public string Estado { get; set; } = string.Empty;
    }

    public class InspeccionResponseDto
    {
        public int Id { get; set; }
        public int UsuarioId { get; set; }
        public string Titulo { get; set; } = string.Empty;
        public string Descripcion { get; set; } = string.Empty;
        public DateTime FechaInspeccion { get; set; }
        public decimal? Latitud { get; set; }
        public decimal? Longitud { get; set; }
        public string Estado { get; set; } = string.Empty;
    }
}